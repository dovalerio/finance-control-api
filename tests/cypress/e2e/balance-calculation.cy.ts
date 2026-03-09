import { categoriesApi, subcategoriesApi, entriesApi, balanceApi } from '../utils/api-client'

const uniqueCatName = () => `Cat-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
const uniqueSubName = () => `Sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

describe('GET /balanco', () => {
  let id_categoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      id_categoria = catResponse.body.id_categoria

      subcategoriesApi
        .create({ nome: uniqueSubName(), id_categoria })
        .then((subResponse) => {
          const id_subcategoria = subResponse.body.id_subcategoria

          entriesApi.create({ valor: 500.0, data: '2026-03-01', id_subcategoria })
          entriesApi.create({ valor: -200.0, data: '2026-03-15', id_subcategoria })
        })
    })
  })

  it('returns 200 with balance fields for a valid date range', () => {
    balanceApi.calculate({ data_inicio: '2026-03-01', data_fim: '2026-03-31' }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.have.property('receita')
      expect(response.body).to.have.property('despesa')
      expect(response.body).to.have.property('saldo')
    })
  })

  it('filters balance by id_categoria and returns categoria in response', () => {
    balanceApi
      .calculate({ data_inicio: '2026-03-01', data_fim: '2026-03-31', id_categoria })
      .then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.have.property('receita')
        expect(response.body).to.have.property('despesa')
        expect(response.body).to.have.property('saldo')
        expect(response.body).to.have.property('categoria')
        expect(response.body.categoria.id_categoria).to.eq(id_categoria)
      })
  })

  it('returns 400 when data_fim is before data_inicio', () => {
    balanceApi
      .calculate({ data_inicio: '2026-03-31', data_fim: '2026-03-01' })
      .then((response) => {
        expect(response.status).to.eq(400)
        expect(response.body.codigo).to.eq('periodo_invalido')
      })
  })

  it('returns 400 when data_inicio is missing', () => {
    balanceApi.calculate({ data_inicio: '', data_fim: '2026-03-31' }).then((response) => {
      expect(response.status).to.be.oneOf([400, 422])
    })
  })

  it('returns 400 when data_fim is missing', () => {
    balanceApi.calculate({ data_inicio: '2026-03-01', data_fim: '' }).then((response) => {
      expect(response.status).to.be.oneOf([400, 422])
    })
  })

  it('returns 401 when api-key header is absent', () => {
    cy.request({
      method: 'GET',
      url: '/balanco',
      qs: { data_inicio: '2026-03-01', data_fim: '2026-03-31' },
      failOnStatusCode: false,
    }).then((response) => {
      expect(response.status).to.eq(401)
    })
  })
})
