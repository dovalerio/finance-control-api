import { categoriesApi } from '../utils/api-client'

const uniqueName = () => `Category-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

describe('GET /categorias', () => {
  it('returns 200 with an array of categories', () => {
    categoriesApi.list().then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters categories by nome query param', () => {
    const nome = uniqueName()

    categoriesApi.create({ nome }).then(() => {
      categoriesApi.list({ nome }).then((response) => {
        expect(response.status).to.eq(200)
        expect(response.body).to.be.an('array')
        expect(response.body.some((c: { nome: string }) => c.nome === nome)).to.be.true
      })
    })
  })
})

describe('GET /categorias/:id_categoria', () => {
  let createdId: number

  before(() => {
    categoriesApi.create({ nome: uniqueName() }).then((response) => {
      createdId = response.body.id_categoria
    })
  })

  it('returns 200 with the category', () => {
    categoriesApi.getById(createdId).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.id_categoria).to.eq(createdId)
      expect(response.body).to.have.property('nome')
    })
  })

  it('returns 404 for a non-existent id', () => {
    categoriesApi.getById(999999).then((response) => {
      expect(response.status).to.eq(404)
    })
  })
})

describe('PUT /categorias/:id_categoria', () => {
  let createdId: number

  before(() => {
    categoriesApi.create({ nome: uniqueName() }).then((response) => {
      createdId = response.body.id_categoria
    })
  })

  it('updates the category and returns 200', () => {
    const updated = { nome: uniqueName() }

    categoriesApi.update(createdId, updated).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.id_categoria).to.eq(createdId)
      expect(response.body.nome).to.eq(updated.nome)
    })
  })
})

describe('DELETE /categorias/:id_categoria', () => {
  it('deletes the category and returns 204', () => {
    categoriesApi.create({ nome: uniqueName() }).then((createResponse) => {
      categoriesApi.remove(createResponse.body.id_categoria).then((response) => {
        expect(response.status).to.eq(204)
      })
    })
  })
})
