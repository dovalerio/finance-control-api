import { categoriesApi, subcategoriesApi } from '../utils/api-client'

const uniqueName = () => `Sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
const uniqueCatName = () => `Cat-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

describe('POST /subcategorias', () => {
  let id_categoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((response) => {
      id_categoria = response.body.id_categoria
    })
  })

  it('creates a subcategory and returns 201 with the resource', () => {
    const nome = uniqueName()

    subcategoriesApi.create({ nome, id_categoria }).then((response) => {
      expect(response.status).to.eq(201)
      expect(response.body).to.have.property('id_subcategoria')
      expect(response.body.nome).to.eq(nome)
      expect(response.body.id_categoria).to.eq(id_categoria)
    })
  })

  it('returns 400 when nome is missing', () => {
    subcategoriesApi.create({ id_categoria } as { nome: string; id_categoria: number }).then((response) => {
      expect(response.status).to.eq(400)
    })
  })

  it('returns 400 when id_categoria is missing', () => {
    subcategoriesApi.create({ nome: uniqueName() } as { nome: string; id_categoria: number }).then((response) => {
      expect(response.status).to.eq(400)
    })
  })

  it('returns 401 when api-key header is absent', () => {
    cy.request({
      method: 'POST',
      url: '/subcategorias',
      body: { nome: uniqueName(), id_categoria: 1 },
      failOnStatusCode: false,
    }).then((response) => {
      expect(response.status).to.eq(401)
    })
  })
})
