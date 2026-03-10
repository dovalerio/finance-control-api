@echo off
setlocal EnableDelayedExpansion

:: ============================================================
:: TESTES DE INTEGRACAO - API CONTROLE FINANCEIRO
::
:: Pre-requisitos:
::   - API rodando em http://localhost:8080
::   - curl disponivel no PATH (Windows 10+ inclui por padrao)
::
:: O script limpa automaticamente dados de teste anteriores.
:: ============================================================

set BASE_URL=http://127.0.0.1:8080/v1
set API_KEY=aXRhw7o=
set WRONG_KEY=chave_invalida

set /a PASS=0
set /a FAIL=0

set CAT_ID=
set SUBCAT_ID=
set ENTRY1_ID=
set ENTRY2_ID=
set ENTRY3_ID=

echo ========================================
echo  TESTES API CONTROLE FINANCEIRO
echo ========================================
echo  Base URL : %BASE_URL%
echo  Data     : %DATE%  %TIME%
echo ========================================

:: Limpa dados de teste anteriores via PowerShell/Invoke-RestMethod
echo  Limpando dados de teste anteriores...
(
  echo $base = '%BASE_URL%'
  echo $h    = @{ 'api-key' = '%API_KEY%' }
  echo $nomes = @^('Alimentacao', 'Alimentacao e Bebidas'^)
  echo try {
  echo     $cats = Invoke-RestMethod -Uri "$base/categorias" -Headers $h -ErrorAction Stop
  echo     foreach ^($c in ^($cats ^| Where-Object { $_.nome -in $nomes }^)^) {
  echo         $subs = Invoke-RestMethod -Uri "$base/subcategorias" -Headers $h
  echo         foreach ^($s in ^($subs ^| Where-Object { $_.id_categoria -eq $c.id_categoria }^)^) {
  echo             $entries = Invoke-RestMethod -Uri "$base/lancamentos" -Headers $h
  echo             foreach ^($e in ^($entries ^| Where-Object { $_.id_subcategoria -eq $s.id_subcategoria }^)^) {
  echo                 Invoke-RestMethod -Method Delete -Uri "$base/lancamentos/$^($e.id_lancamento^)" -Headers $h -EA 0 ^| Out-Null
  echo             }
  echo             Invoke-RestMethod -Method Delete -Uri "$base/subcategorias/$^($s.id_subcategoria^)" -Headers $h -EA 0 ^| Out-Null
  echo         }
  echo         Invoke-RestMethod -Method Delete -Uri "$base/categorias/$^($c.id_categoria^)" -Headers $h -EA 0 ^| Out-Null
  echo     }
  echo } catch {}
) > _cleanup.ps1
powershell -NoProfile -File _cleanup.ps1 2>nul
del _cleanup.ps1 2>nul
echo  Limpeza concluida.

echo.
echo ^>^>^> [1/6] AUTENTICACAO
call :test_auth_missing
call :test_auth_wrong_key

echo.
echo ^>^>^> [2/6] CATEGORIAS
call :test_create_category
call :test_create_category_missing_name
call :test_create_category_duplicate
call :test_list_categories
call :test_list_categories_by_name
call :test_get_category_by_id
call :test_update_category
call :test_get_category_not_found
call :test_update_category_not_found

echo.
echo ^>^>^> [3/6] SUBCATEGORIAS
call :test_create_subcategory
call :test_create_subcategory_missing_name
call :test_create_subcategory_missing_id_categoria
call :test_create_subcategory_invalid_category
call :test_list_subcategories
call :test_list_subcategories_by_name
call :test_get_subcategory_by_id
call :test_update_subcategory
call :test_get_subcategory_not_found
call :test_update_subcategory_not_found

echo.
echo ^>^>^> [4/6] LANCAMENTOS
call :test_create_entry_income
call :test_create_entry_expense
call :test_create_entry_with_comment
call :test_create_entry_zero_valor
call :test_create_entry_missing_valor
call :test_create_entry_missing_subcategory
call :test_create_entry_invalid_subcategory
call :test_list_entries
call :test_list_entries_by_subcategory
call :test_list_entries_by_period
call :test_get_entry_by_id
call :test_update_entry
call :test_get_entry_not_found
call :test_update_entry_not_found

echo.
echo ^>^>^> [5/6] BALANCO
call :test_balance
call :test_balance_with_category_filter
call :test_balance_negative_saldo
call :test_balance_invalid_period
call :test_balance_missing_params

echo.
echo ^>^>^> [6/6] EXCLUSAO
call :test_delete_entry_not_found
call :test_delete_subcategory_with_entries
call :test_delete_entry
call :test_delete_subcategory_not_found
call :test_delete_subcategory
call :test_delete_category_not_found
call :test_cascade_delete_category
call :test_delete_category

echo.
echo ========================================
echo  RESULTADO FINAL
echo ========================================
echo  PASSOU : !PASS!
echo  FALHOU : !FAIL!
set /a TOTAL=!PASS!+!FAIL!
echo  TOTAL  : !TOTAL!
echo ========================================

exit /b 0

:: ============================================================
:: HELPERS
:: ============================================================

:ok
set "_ok_msg=%~1"
echo   [PASS] !_ok_msg!
set /a PASS+=1
exit /b

:nok
set "_nok_msg=%~1"
set "_nok_exp=%~2"
set "_nok_rec=%~3"
echo   [FAIL] !_nok_msg!
echo          esperado=!_nok_exp!  recebido=!_nok_rec!
if exist response.txt (
    echo          resposta:
    type response.txt
)
set /a FAIL+=1
exit /b

:check
set "_chk_exp=%~1"
set "_chk_name=%~2"
if "!STATUS!"=="!_chk_exp!" (
    call :ok "!_chk_name!"
) else (
    call :nok "!_chk_name!" "!_chk_exp!" "!STATUS!"
)
exit /b

:check_body
set "_cb_str=%~1"
set "_cb_name=%~2"
findstr /C:"!_cb_str!" response.txt >nul 2>&1
if !ERRORLEVEL!==0 (
    call :ok "!_cb_name!"
) else (
    call :nok "!_cb_name!" "[!_cb_str!]" "ausente na resposta"
)
exit /b

:: ============================================================
:: [1/6] AUTENTICACAO
:: ============================================================

:test_auth_missing
curl -s -o response.txt -w "%%{http_code}" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 401 "autenticacao: requisicao sem api-key retorna 401"
exit /b

:test_auth_wrong_key
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %WRONG_KEY%" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 401 "autenticacao: api-key invalida retorna 401"
exit /b

:: ============================================================
:: [2/6] CATEGORIAS
:: ============================================================

:test_create_category
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Alimentacao\"}" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 201 "categoria: criar com dados validos retorna 201"
if "!STATUS!"=="201" (
    powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_categoria" > id_tmp.txt 2>nul
    set /p CAT_ID=<id_tmp.txt
    del id_tmp.txt 2>nul
    echo          CAT_ID=!CAT_ID!
    call :check_body "id_categoria" "categoria: resposta contem campo id_categoria"
    call :check_body "nome" "categoria: resposta contem campo nome"
)
exit /b

:test_create_category_missing_name
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{}" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 400 "categoria: criar sem nome retorna 400"
call :check_body "erro_validacao" "categoria: erro 400 contem codigo erro_validacao"
exit /b

:test_create_category_duplicate
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Alimentacao\"}" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 409 "categoria: nome duplicado retorna 409"
call :check_body "conflito" "categoria: erro 409 contem codigo conflito"
exit /b

:test_list_categories
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
call :check 200 "categoria: listar todas retorna 200"
exit /b

:test_list_categories_by_name
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/categorias?nome=Alimentacao" > status.txt
set /p STATUS=<status.txt
call :check 200 "categoria: filtrar por nome retorna 200"
exit /b

:test_get_category_by_id
if "!CAT_ID!"=="" (
    call :nok "categoria: buscar por id" "CAT_ID valido" "CAT_ID vazio - criacao falhou"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias/!CAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "categoria: buscar por id retorna 200"
exit /b

:test_update_category
if "!CAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Alimentacao e Bebidas\"}" ^
  %BASE_URL%/categorias/!CAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "categoria: atualizar retorna 200"
exit /b

:test_get_category_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "categoria: buscar id inexistente retorna 404"
call :check_body "nao_encontrado" "categoria: erro 404 contem codigo nao_encontrado"
exit /b

:test_update_category_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Inexistente\"}" ^
  %BASE_URL%/categorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "categoria: atualizar id inexistente retorna 404"
exit /b

:: ============================================================
:: [3/6] SUBCATEGORIAS
:: ============================================================

:test_create_subcategory
if "!CAT_ID!"=="" (
    call :nok "subcategoria: criar" "CAT_ID valido" "CAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Mercado\",\"id_categoria\":!CAT_ID!}" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
call :check 201 "subcategoria: criar com dados validos retorna 201"
if "!STATUS!"=="201" (
    powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_subcategoria" > id_tmp.txt 2>nul
    set /p SUBCAT_ID=<id_tmp.txt
    del id_tmp.txt 2>nul
    echo          SUBCAT_ID=!SUBCAT_ID!
    call :check_body "id_subcategoria" "subcategoria: resposta contem campo id_subcategoria"
    call :check_body "id_categoria" "subcategoria: resposta contem campo id_categoria"
)
exit /b

:test_create_subcategory_missing_name
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"id_categoria\":1}" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
call :check 400 "subcategoria: criar sem nome retorna 400"
call :check_body "erro_validacao" "subcategoria: erro 400 contem codigo erro_validacao"
exit /b

:test_create_subcategory_missing_id_categoria
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Teste\"}" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
call :check 400 "subcategoria: criar sem id_categoria retorna 400"
exit /b

:test_create_subcategory_invalid_category
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Gasolina\",\"id_categoria\":999999}" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
call :check 404 "subcategoria: id_categoria inexistente retorna 404"
exit /b

:test_list_subcategories
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
call :check 200 "subcategoria: listar todas retorna 200"
exit /b

:test_list_subcategories_by_name
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/subcategorias?nome=Mercado" > status.txt
set /p STATUS=<status.txt
call :check 200 "subcategoria: filtrar por nome retorna 200"
exit /b

:test_get_subcategory_by_id
if "!SUBCAT_ID!"=="" (
    call :nok "subcategoria: buscar por id" "SUBCAT_ID valido" "SUBCAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/!SUBCAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "subcategoria: buscar por id retorna 200"
exit /b

:test_update_subcategory
if "!SUBCAT_ID!"=="" exit /b
if "!CAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Supermercado\",\"id_categoria\":!CAT_ID!}" ^
  %BASE_URL%/subcategorias/!SUBCAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "subcategoria: atualizar retorna 200"
exit /b

:test_get_subcategory_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "subcategoria: buscar id inexistente retorna 404"
call :check_body "nao_encontrado" "subcategoria: erro 404 contem codigo nao_encontrado"
exit /b

:test_update_subcategory_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Inexistente\",\"id_categoria\":1}" ^
  %BASE_URL%/subcategorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "subcategoria: atualizar id inexistente retorna 404"
exit /b

:: ============================================================
:: [4/6] LANCAMENTOS
:: ============================================================

:test_create_entry_income
if "!SUBCAT_ID!"=="" (
    call :nok "lancamento: criar receita" "SUBCAT_ID valido" "SUBCAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":1500.00,\"data\":\"2026-01-10\",\"id_subcategoria\":!SUBCAT_ID!}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 201 "lancamento: criar receita (valor positivo) retorna 201"
if "!STATUS!"=="201" (
    powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_lancamento" > id_tmp.txt 2>nul
    set /p ENTRY1_ID=<id_tmp.txt
    del id_tmp.txt 2>nul
    echo          ENTRY1_ID=!ENTRY1_ID!
    call :check_body "id_lancamento" "lancamento: resposta contem campo id_lancamento"
    call :check_body "valor" "lancamento: resposta contem campo valor"
    call :check_body "data" "lancamento: resposta contem campo data"
    call :check_body "id_subcategoria" "lancamento: resposta contem campo id_subcategoria"
)
exit /b

:test_create_entry_expense
if "!SUBCAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":-300.00,\"data\":\"2026-01-15\",\"id_subcategoria\":!SUBCAT_ID!}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 201 "lancamento: criar despesa (valor negativo) retorna 201"
if "!STATUS!"=="201" (
    powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_lancamento" > id_tmp.txt 2>nul
    set /p ENTRY2_ID=<id_tmp.txt
    del id_tmp.txt 2>nul
    echo          ENTRY2_ID=!ENTRY2_ID!
)
exit /b

:test_create_entry_with_comment
if "!SUBCAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":-2000.00,\"data\":\"2026-01-20\",\"id_subcategoria\":!SUBCAT_ID!,\"comentario\":\"Despesa extra do mes\"}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 201 "lancamento: criar com comentario opcional retorna 201"
if "!STATUS!"=="201" (
    powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_lancamento" > id_tmp.txt 2>nul
    set /p ENTRY3_ID=<id_tmp.txt
    del id_tmp.txt 2>nul
    echo          ENTRY3_ID=!ENTRY3_ID!
    call :check_body "comentario" "lancamento: comentario presente na resposta"
)
exit /b

:test_create_entry_missing_valor
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"id_subcategoria\":1}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 400 "lancamento: criar sem valor retorna 400"
call :check_body "erro_validacao" "lancamento: erro 400 contem codigo erro_validacao"
exit /b

:test_create_entry_missing_subcategory
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":100.00}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 400 "lancamento: criar sem id_subcategoria retorna 400"
exit /b

:test_create_entry_invalid_subcategory
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":100.00,\"data\":\"2026-01-01\",\"id_subcategoria\":999999}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 404 "lancamento: id_subcategoria inexistente retorna 404"
exit /b

:test_list_entries
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 200 "lancamento: listar todos retorna 200"
exit /b

:test_list_entries_by_subcategory
if "!SUBCAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/lancamentos?id_subcategoria=!SUBCAT_ID!" > status.txt
set /p STATUS=<status.txt
call :check 200 "lancamento: filtrar por id_subcategoria retorna 200"
exit /b

:test_list_entries_by_period
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/lancamentos?data_inicio=2026-01-01&data_fim=2026-01-31" > status.txt
set /p STATUS=<status.txt
call :check 200 "lancamento: filtrar por periodo (data_inicio + data_fim) retorna 200"
exit /b

:test_get_entry_by_id
if "!ENTRY1_ID!"=="" (
    call :nok "lancamento: buscar por id" "ENTRY1_ID valido" "ENTRY1_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/lancamentos/!ENTRY1_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "lancamento: buscar por id retorna 200"
exit /b

:test_update_entry
if "!ENTRY1_ID!"=="" exit /b
if "!SUBCAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":1800.00,\"data\":\"2026-01-10\",\"id_subcategoria\":!SUBCAT_ID!}" ^
  %BASE_URL%/lancamentos/!ENTRY1_ID! > status.txt
set /p STATUS=<status.txt
call :check 200 "lancamento: atualizar retorna 200"
exit /b

:test_get_entry_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/lancamentos/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "lancamento: buscar id inexistente retorna 404"
call :check_body "nao_encontrado" "lancamento: erro 404 contem codigo nao_encontrado"
exit /b

:test_update_entry_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X PUT ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":100.00,\"id_subcategoria\":1}" ^
  %BASE_URL%/lancamentos/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "lancamento: atualizar id inexistente retorna 404"
exit /b

:: ============================================================
:: [5/6] BALANCO
:: receita=1800 (apos update), despesa=2300, saldo=-500 (negativo)
:: ============================================================

:test_balance
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/balanco?data_inicio=2026-01-01&data_fim=2026-01-31" > status.txt
set /p STATUS=<status.txt
call :check 200 "balanco: calcular por periodo retorna 200"
if "!STATUS!"=="200" (
    call :check_body "receita" "balanco: resposta contem campo receita"
    call :check_body "despesa" "balanco: resposta contem campo despesa"
    call :check_body "saldo" "balanco: resposta contem campo saldo"
)
exit /b

:test_balance_with_category_filter
if "!CAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/balanco?data_inicio=2026-01-01&data_fim=2026-01-31&id_categoria=!CAT_ID!" > status.txt
set /p STATUS=<status.txt
call :check 200 "balanco: filtrar por id_categoria retorna 200"
exit /b

:test_balance_negative_saldo
:: Com receita=1800 e despesa=2300, saldo deve ser -500
:: Valida que saldo pode ser negativo (contrato da API)
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/balanco?data_inicio=2026-01-01&data_fim=2026-01-31" > status.txt
set /p STATUS=<status.txt
call :check 200 "balanco: saldo negativo (despesa > receita) retorna 200"
if "!STATUS!"=="200" (
    powershell -NoProfile -Command "try { $r = Get-Content response.txt | ConvertFrom-Json; if ($r.saldo -lt 0) { 'OK' } else { 'saldo=' + $r.saldo + ' nao e negativo' } } catch { 'ERRO_PARSE' }" > saldo_tmp.txt 2>nul
    set /p SALDO_CHK=<saldo_tmp.txt
    del saldo_tmp.txt 2>nul
    if "!SALDO_CHK!"=="OK" (
        call :ok "balanco: saldo e negativo quando despesas superam receitas"
    ) else (
        call :nok "balanco: saldo e negativo quando despesas superam receitas" "saldo negativo" "!SALDO_CHK!"
    )
)
exit /b

:test_balance_invalid_period
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  "%BASE_URL%/balanco?data_inicio=2026-02-01&data_fim=2026-01-01" > status.txt
set /p STATUS=<status.txt
call :check 400 "balanco: data_inicio maior que data_fim retorna 400"
exit /b

:test_balance_missing_params
curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/balanco > status.txt
set /p STATUS=<status.txt
call :check 400 "balanco: sem data_inicio e data_fim retorna 400"
exit /b

:: ============================================================
:: [6/6] EXCLUSAO
:: ============================================================

:test_delete_entry_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/lancamentos/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "lancamento: deletar id inexistente retorna 404"
exit /b

:test_delete_entry
if "!ENTRY1_ID!"=="" (
    call :nok "lancamento: deletar" "ENTRY1_ID valido" "ENTRY1_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/lancamentos/!ENTRY1_ID! > status.txt
set /p STATUS=<status.txt
call :check 204 "lancamento: deletar por id retorna 204"
:: Remove demais lancamentos para liberar a subcategoria
if not "!ENTRY2_ID!"=="" (
    curl -s -o nul -X DELETE -H "api-key: %API_KEY%" %BASE_URL%/lancamentos/!ENTRY2_ID! >nul 2>&1
)
if not "!ENTRY3_ID!"=="" (
    curl -s -o nul -X DELETE -H "api-key: %API_KEY%" %BASE_URL%/lancamentos/!ENTRY3_ID! >nul 2>&1
)
exit /b

:test_delete_subcategory_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "subcategoria: deletar id inexistente retorna 404"
exit /b

:test_delete_subcategory
if "!SUBCAT_ID!"=="" (
    call :nok "subcategoria: deletar" "SUBCAT_ID valido" "SUBCAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/!SUBCAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 204 "subcategoria: deletar por id retorna 204"
exit /b

:test_delete_category_not_found
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias/999999 > status.txt
set /p STATUS=<status.txt
call :check 404 "categoria: deletar id inexistente retorna 404"
exit /b

:test_delete_category
if "!CAT_ID!"=="" (
    call :nok "categoria: deletar" "CAT_ID valido" "CAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias/!CAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 204 "categoria: deletar por id retorna 204"
exit /b

:: ============================================================
:: [4] Extra — valor zero
:: ============================================================

:test_create_entry_zero_valor
if "!SUBCAT_ID!"=="" exit /b
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"valor\":0,\"data\":\"2026-01-01\",\"id_subcategoria\":!SUBCAT_ID!}" ^
  %BASE_URL%/lancamentos > status.txt
set /p STATUS=<status.txt
call :check 422 "lancamento: criar com valor zero retorna 422"
call :check_body "valor_invalido" "lancamento: erro 422 contem codigo valor_invalido"
exit /b

:: ============================================================
:: [6] Extra — delete subcategoria com lancamentos e cascade
:: ============================================================

:test_delete_subcategory_with_entries
:: Tenta deletar SUBCAT_ID enquanto ainda tem lancamentos atrelados (antes de test_delete_entry)
if "!SUBCAT_ID!"=="" (
    call :nok "subcategoria: deletar com lancamentos" "SUBCAT_ID valido" "SUBCAT_ID vazio"
    exit /b
)
curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/!SUBCAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 409 "subcategoria: deletar com lancamentos retorna 409"
call :check_body "conflito" "subcategoria: erro 409 contem codigo conflito"
exit /b

:test_cascade_delete_category
:: Cria categoria e subcategoria temporarias, deleta a categoria e verifica que a sub sumiu
curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"CatCascadeTest\"}" ^
  %BASE_URL%/categorias > status.txt
set /p STATUS=<status.txt
if not "!STATUS!"=="201" (
    call :nok "categoria: exclusao em cascata cria categoria" "201" "!STATUS!"
    exit /b
)
powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_categoria" > id_tmp.txt 2>nul
set /p CASC_CAT_ID=<id_tmp.txt
del id_tmp.txt 2>nul

curl -s -o response.txt -w "%%{http_code}" ^
  -X POST ^
  -H "api-key: %API_KEY%" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"SubCascadeTest\",\"id_categoria\":!CASC_CAT_ID!}" ^
  %BASE_URL%/subcategorias > status.txt
set /p STATUS=<status.txt
if not "!STATUS!"=="201" (
    call :nok "categoria: exclusao em cascata cria subcategoria" "201" "!STATUS!"
    exit /b
)
powershell -NoProfile -Command "(Get-Content response.txt | ConvertFrom-Json).id_subcategoria" > id_tmp.txt 2>nul
set /p CASC_SUB_ID=<id_tmp.txt
del id_tmp.txt 2>nul

curl -s -o response.txt -w "%%{http_code}" ^
  -X DELETE ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/categorias/!CASC_CAT_ID! > status.txt
set /p STATUS=<status.txt
call :check 204 "categoria: exclusao em cascata retorna 204"

curl -s -o response.txt -w "%%{http_code}" ^
  -H "api-key: %API_KEY%" ^
  %BASE_URL%/subcategorias/!CASC_SUB_ID! > status.txt
set /p STATUS=<status.txt
call :check 404 "categoria: exclusao em cascata remove subcategorias (sub retorna 404)"
exit /b