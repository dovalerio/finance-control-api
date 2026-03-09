CREATE TABLE categoria (
    id_categoria  BIGSERIAL    PRIMARY KEY,
    nome          VARCHAR(255) NOT NULL,
    CONSTRAINT uk_categoria_nome UNIQUE (nome)
);

CREATE TABLE subcategoria (
    id_subcategoria  BIGSERIAL    PRIMARY KEY,
    nome             VARCHAR(255) NOT NULL,
    id_categoria     BIGINT       NOT NULL,
    CONSTRAINT fk_subcategoria_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE CASCADE,
    CONSTRAINT uk_subcategoria_nome_categoria UNIQUE (nome, id_categoria)
);

CREATE TABLE lancamento (
    id_lancamento    BIGSERIAL      PRIMARY KEY,
    comentario       VARCHAR(255)   NOT NULL DEFAULT '',
    valor            NUMERIC(19, 2) NOT NULL,
    tipo             VARCHAR(20)    NOT NULL,
    data             DATE           NOT NULL,
    id_categoria     BIGINT         NOT NULL,
    id_subcategoria  BIGINT,
    CONSTRAINT fk_lancamento_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE CASCADE,
    CONSTRAINT fk_lancamento_subcategoria
        FOREIGN KEY (id_subcategoria) REFERENCES subcategoria(id_subcategoria) ON DELETE SET NULL,
    CONSTRAINT ck_lancamento_tipo CHECK (tipo IN ('INCOME', 'EXPENSE')),
    CONSTRAINT ck_lancamento_valor CHECK (valor <> 0)
);

CREATE INDEX idx_subcategoria_id_categoria  ON subcategoria (id_categoria);
CREATE INDEX idx_lancamento_id_categoria    ON lancamento (id_categoria);
CREATE INDEX idx_lancamento_id_subcategoria ON lancamento (id_subcategoria);
CREATE INDEX idx_lancamento_data            ON lancamento (data);
CREATE INDEX idx_lancamento_data_categoria  ON lancamento (data, id_categoria);
