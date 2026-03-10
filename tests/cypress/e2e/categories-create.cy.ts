import { categoriesApi } from '../utils/api-client'

const uniqueName = () => `Category-${Date.now()}`

describe('POST /categorias', () => {
  it('creates a category and returns 201 with the resource', () => {
    const payload = { nome: uniqueName() }

    categoriesApi.create(payload).then((response) => {
      expect(response.status).to.eq(201)
      expect(response.body).to.have.property('id_categoria')
      expect(response.body.nome).to.eq(payload.nome)
    })
  })

  it('returns 400 with erro_validacao when nome is missing', () => {
    categoriesApi.create({} as { nome: string }).then((response) => {
      expect(response.status).to.eq(400)
      expect(response.body.codigo).to.eq('erro_validacao')
    })
  })

  it('returns 409 when nome already exists', () => {
    const payload = { nome: uniqueName() }

    categoriesApi.create(payload).then(() => {
      categoriesApi.create(payload).then((response) => {
        expect(response.status).to.eq(409)
      })
    })
  })

  it('returns 401 when api-key header is absent', () => {
    cy.request({
      method: 'POST',
      url: '/categorias',
      body: { nome: uniqueName() },
      failOnStatusCode: false,
    }).then((response) => {
      expect(response.status).to.eq(401)
    })
  })
})
