import { categoriesApi, subcategoriesApi, entriesApi } from '../utils/api-client'

const uniqueCatName = () => `Cat-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
const uniqueSubName = () => `Sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

describe('POST /lancamentos', () => {
  let id_subcategoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      subcategoriesApi
        .create({ nome: uniqueSubName(), id_categoria: catResponse.body.id_categoria })
        .then((subResponse) => {
          id_subcategoria = subResponse.body.id_subcategoria
        })
    })
  })

  it('creates an entry with all fields and returns 201', () => {
    const payload = {
      valor: 150.0,
      data: '2026-03-07',
      id_subcategoria,
      comentario: 'Lunch',
    }

    entriesApi.create(payload).then((response) => {
      expect(response.status).to.eq(201)
      expect(response.body).to.have.property('id_lancamento')
      expect(response.body.valor).to.eq(payload.valor)
      expect(response.body.id_subcategoria).to.eq(id_subcategoria)
      expect(response.body.comentario).to.eq(payload.comentario)
    })
  })

  it('creates an entry without optional fields and returns 201', () => {
    entriesApi.create({ valor: 50.0, id_subcategoria }).then((response) => {
      expect(response.status).to.eq(201)
      expect(response.body).to.have.property('id_lancamento')
      expect(response.body.valor).to.eq(50.0)
    })
  })

  it('returns 400 when valor is missing', () => {
    entriesApi.create({ id_subcategoria } as { valor: number; id_subcategoria: number }).then((response) => {
      expect(response.status).to.eq(400)
    })
  })

  it('returns 400 when id_subcategoria is missing', () => {
    entriesApi.create({ valor: 100 } as { valor: number; id_subcategoria: number }).then((response) => {
      expect(response.status).to.eq(400)
    })
  })

  it('returns 422 when valor is zero', () => {
    entriesApi.create({ valor: 0, id_subcategoria }).then((response) => {
      expect(response.status).to.eq(422)
      expect(response.body.codigo).to.eq('valor_invalido')
    })
  })

  it('returns 401 when api-key header is absent', () => {
    cy.request({
      method: 'POST',
      url: '/lancamentos',
      body: { valor: 100, id_subcategoria: 1 },
      failOnStatusCode: false,
    }).then((response) => {
      expect(response.status).to.eq(401)
    })
  })
})
