package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_PRODUCT'])
@Transactional(readOnly = true)
class ProductController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
  //todo search oth. attr.
  //todo details
  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = Product.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
      if (params.getLong('productGroup.id')) eq('productGroup', ProductGroup.get(params.getLong('productGroup.id')))
      if (params.getLong('uom.id')) eq('uom', Uom.get(params.getLong('uom.id')))
      if (params.status == '1') or {eq('cancel', false); isNull('cancel')}
      if (params.status == '2') eq('cancel', true)

    }
    [productInstanceList: list, productInstanceCount: list.totalCount]
  }

  def show(Product productInstance) {
    [productInstance: productInstance, productPrices: getCmd(productInstance).productPrices]
  }

  def create() {
    def productInstance = new Product(params)
    //[productInstance: new Product(params)]
    [productInstance : productInstance, cmd: getCmd(productInstance)]
  }

  @Transactional
  def save(ProductCmd cmd) {
    if (cmd == null) {
      notFound()
      return
    }

    def productInstance = new Product()
    mapProperties(productInstance, cmd)

    productInstance.validate()
    //validate(productInstance)
    if (productInstance.hasErrors()) {
      respond productInstance.errors, view: 'create', model: [cmd: cmd]
      return
    }

    productInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'product.label', default: 'product'), productInstance])
        redirect productInstance
      }
      '*' { respond productInstance, [status: CREATED] }
    }
  }

  def edit(Product productInstance) {
    [productInstance : productInstance, cmd: getCmd(productInstance)]
  }

  @Transactional
  def update() {//def update(ProductCmd cmd) <- bug cmd == null
    ProductCmd cmd = new ProductCmd()
    cmd.properties = params


    def productInstance = Product.get(cmd.id)
    mapProperties(productInstance, cmd)

    productInstance.validate()
    if (productInstance.hasErrors()) {
      respond productInstance.errors, view: 'edit', model: [cmd: cmd]
      return
    }

    productInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'product'), productInstance])
        redirect productInstance
      }
      '*' { respond productInstance, [status: OK] }
    }
  }

  @Transactional
  def toggleCancel(Product productInstance) {//def update(ProductCmd cmd) <- bug cmd == null
    productInstance.cancel = !productInstance.cancel

    productInstance.validate()
    if (productInstance.hasErrors()) {
      respond productInstance.errors, view: 'show', model: [id: productInstance.id]
      return
    }

    productInstance.save flush: true

    flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'product'), productInstance])
    redirect productInstance
  }

  @Transactional
  def delete(Product productInstance) {
    if (productInstance == null) {
      notFound()
      return
    }

    productInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'product.label', default: 'product'), productInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }

  private getCmd(Product product) {
    def cmd = new ProductCmd()
    cmd.id = product.id
    cmd.version = product.version
    cmd.code = product.code
    cmd.name = product.name
    cmd.productGroup = product.productGroup
    cmd.uom = product.uom
    cmd.minimumStock = product.minimumStock
    cmd.productPrices = []
    PriceType.values().each { priceType ->
      def productPrice = new ProductPriceCmd()
      productPrice.priceType = priceType
      productPrice.price = product.id ? (
          ProductPrice.createCriteria().get {
            eq('product', product)
            eq('priceType', priceType)
          }?.price ?: 0
      ) : 0
      cmd.productPrices << productPrice
    }

    cmd
  }

  private mapProperties(Product productInstance, ProductCmd cmd) {
    productInstance.code = cmd.code
    productInstance.name = cmd.name
    productInstance.productGroup = cmd.productGroup
    productInstance.uom = cmd.uom
    productInstance.minimumStock = cmd.minimumStock
    productInstance.cancel = cmd.cancel
    if (!productInstance.id) productInstance.creator = currentUser //create
    productInstance.updater = currentUser
    cmd.productPrices.each { productPriceCmd ->
      ProductPrice productPrice
      if (productInstance.id) {
        productPrice = ProductPrice.createCriteria().get {
          eq('product', productInstance)
          eq('priceType', productPriceCmd.priceType)
        }
      }
      if (!productPrice) {
        productPrice = new ProductPrice()
        productPrice.priceType = productPriceCmd.priceType
        productInstance.addToProductPrices(productPrice)
      }
      productPrice.price = productPriceCmd.price
    }
  }
}

class ProductCmd {
  Long id
  Long version
  String code
  String name
  ProductGroup productGroup
  Uom uom
  BigDecimal minimumStock
  Boolean cancel
  List<ProductPriceCmd> productPrices
}

class ProductPriceCmd {
  PriceType priceType
  BigDecimal price
}