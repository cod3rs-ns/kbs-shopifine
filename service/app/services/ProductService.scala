package services

import com.google.inject.Inject
import domain.{Product, ProductStatus}
import repositories.ProductRepository

import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject()(repository: ProductRepository)
                              (implicit val ec: ExecutionContext) {

  import ProductService._

  def save(product: Product): Future[Product] =
    repository.save(product)

  def retrieveOne(id: Long): Future[Option[Product]] =
    repository.retrieve(id)

  def retrieveProductsWithFilters(filters: Map[String, String],
                                  offset: Int,
                                  limit: Int): Future[Seq[Product]] = {
    repository.retrieveAll().map(products => {
      products.filter(product => {
        filters.keys.forall(name => {
          val value = filters(name)
          name match {
            case Name => product.name.toLowerCase.contains(value.toLowerCase)
            case Category => product.categoryId == value.toLong
            case PriceMin => product.price >= value.toDouble
            case PriceMax => product.price <= value.toDouble
            case IsActive => product.status == ProductStatus.valueOf(value.toUpperCase)
            case _ => true
          }
        })
      }).slice(offset, offset + limit)
    })
  }

  def fillStock(id: Long, quantity: Long): Future[Int] =
    repository.fillStock(id, quantity)

  def boughtNow(id: Long): Future[Int] =
    repository.updateLastBoughtDateTime(id)

  def outOfStock(id: Long): Future[Int] =
    repository.updateFillStock(id)
}

object ProductService {
  val Name: String = "name"
  val Category: String = "category"
  val PriceMin: String = "price-range-from"
  val PriceMax: String = "price-range-to"
  val IsActive: String = "active"
}
