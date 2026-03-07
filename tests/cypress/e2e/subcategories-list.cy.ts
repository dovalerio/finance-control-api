import { categoriesApi, subcategoriesApi } from '../utils/api-client'

const uniqueName = () => `Sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
const uniqueCatName = () => `Cat-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`

describe('GET /subcategorias', () => {
  let id_categoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      id_categoria = catResponse.body.id_categoria
      subcategoriesApi.create({ nome: uniqueName(), id_categoria })
    })
  })

  it('returns 200 with an array of subcategories', () => {
    subcategoriesApi.list().then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters subcategories by id_categoria', () => {
    subcategoriesApi.list({ id_categoria }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body).to.be.an('array')
    })
  })

  it('filters subcategories by nome', () => {
    const nome = uniqueName()

    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      subcategoriesApi.create({ nome, id_categoria: catResponse.body.id_categoria }).then(() => {
        subcategoriesApi.list({ nome }).then((response) => {
          expect(response.status).to.eq(200)
          expect(response.body).to.be.an('array')
          expect(response.body.some((s: { nome: string }) => s.nome === nome)).to.be.true
        })
      })
    })
  })
})

describe('GET /subcategorias/:id_subcategoria', () => {
  let id_subcategoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      subcategoriesApi
        .create({ nome: uniqueName(), id_categoria: catResponse.body.id_categoria })
        .then((subResponse) => {
          id_subcategoria = subResponse.body.id_subcategoria
        })
    })
  })

  it('returns 200 with the subcategory', () => {
    subcategoriesApi.getById(id_subcategoria).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.id_subcategoria).to.eq(id_subcategoria)
      expect(response.body).to.have.property('nome')
      expect(response.body).to.have.property('id_categoria')
    })
  })

  it('returns 404 for a non-existent id', () => {
    subcategoriesApi.getById(999999).then((response) => {
      expect(response.status).to.eq(404)
    })
  })
})

describe('PUT /subcategorias/:id_subcategoria', () => {
  let id_subcategoria: number
  let id_categoria: number

  before(() => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      id_categoria = catResponse.body.id_categoria

      subcategoriesApi.create({ nome: uniqueName(), id_categoria }).then((subResponse) => {
        id_subcategoria = subResponse.body.id_subcategoria
      })
    })
  })

  it('updates the subcategory and returns 200', () => {
    const newNome = uniqueName()

    subcategoriesApi.update(id_subcategoria, { nome: newNome, id_categoria }).then((response) => {
      expect(response.status).to.eq(200)
      expect(response.body.id_subcategoria).to.eq(id_subcategoria)
      expect(response.body.nome).to.eq(newNome)
    })
  })
})

describe('DELETE /subcategorias/:id_subcategoria', () => {
  it('deletes the subcategory and returns 204', () => {
    categoriesApi.create({ nome: uniqueCatName() }).then((catResponse) => {
      subcategoriesApi
        .create({ nome: uniqueName(), id_categoria: catResponse.body.id_categoria })
        .then((subResponse) => {
          subcategoriesApi.remove(subResponse.body.id_subcategoria).then((response) => {
            expect(response.status).to.eq(204)
          })
        })
    })
  })
})
