import { categoriesApi, subcategoriesApi, entriesApi } from '../utils/api-client'

const uniqueCatName = () => `Cat-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
const uniqueSubName = () => `Sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

const setupSubcategoryWithEntry = (
  onReady: (id_subcategoria: number, id_lancamento: number) => void
) => {
  categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
    subcategoriesApi
      .create({ nome: uniqueSubName(), id_categoria: catResponse.body.id_categoria })
      .then((subResponse) => {
        const id_subcategoria = subResponse.body.id_subcategoria

        entriesApi
          .create({ valor: 200.0, data: '07/03/2026', id_subcategoria, comentario: 'Setup entry' })
          .then((entryResponse) => {
            onReady(id_subcategoria, entryResponse.body.id_lancamento)
          })
      })
  })
}

describe('GET /lancamentos', () => {
  let id_subcategoria: number

  before(() => {
    setupSubcategoryWithEntry((subId) => {
      id_subcategoria = subId
    })
  })

  it('returns 200 with an array of entries', () => {
    entriesApi.list().then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters entries by id_subcategoria', () => {
    entriesApi.list({ id_subcategoria }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters entries by date range', () => {
    entriesApi.list({ data_inicio: '2026-01-01', data_fim: '2026-12-31' }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters entries by id_subcategoria and date range combined', () => {
    entriesApi
      .list({ id_subcategoria, data_inicio: '2026-01-01', data_fim: '2026-12-31' })
      .then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.be.an('array')
      })
  })
})

describe('GET /lancamentos/:id_lancamento', () => {
  let id_lancamento: number

  before(() => {
    setupSubcategoryWithEntry((_subId, eId) => {
      id_lancamento = eId
    })
  })

  it('returns 200 with the entry', () => {
    entriesApi.getById(id_lancamento).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.id_lancamento).to.eq(id_lancamento)
      expect(response.body).to.have.property('valor')
      expect(response.body).to.have.property('id_subcategoria')
    })
  })

  it('returns 404 for a non-existent id', () => {
    entriesApi.getById(999999).then((response) => {
      expect(response.status).to.eq(404)
    })
  })
})

describe('PUT /lancamentos/:id_lancamento', () => {
  let id_lancamento: number
  let id_subcategoria: number

  before(() => {
    setupSubcategoryWithEntry((subId, eId) => {
      id_subcategoria = subId
      id_lancamento = eId
    })
  })

  it('updates the entry and returns 200', () => {
    entriesApi.update(id_lancamento, { valor: 999.0, id_subcategoria }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.valor).to.eq(999.0)
      expect(response.body.id_lancamento).to.eq(id_lancamento)
    })
  })
})

describe('DELETE /lancamentos/:id_lancamento', () => {
  it('deletes the entry and returns 204', () => {
    setupSubcategoryWithEntry((_subId, id_lancamento) => {
      entriesApi.remove(id_lancamento).then((response) => {
        expect(response.status).to.eq(204)
      })
    })
  })
})
