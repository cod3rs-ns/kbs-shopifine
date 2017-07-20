import com.google.inject.AbstractModule
import db._
import play.api.libs.concurrent.AkkaGuiceSupport
import repositories._

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MySqlUserRepository])
    bind(classOf[ProductRepository]).to(classOf[MySqlProductRepository])
    bind(classOf[ProductCategoryRepository]).to(classOf[MySqlProductCategoryRepository])
    bind(classOf[BuyerCategoryRepository]).to(classOf[MySqlBuyerCategoryRepository])
    bind(classOf[ConsumptionThresholdRepository]).to(classOf[MySqlConsumptionThresholdRepository])
    bind(classOf[BillRepository]).to(classOf[MySqlBillRepository])
    bind(classOf[BillItemRepository]).to(classOf[MySqlBillItemRepository])
  }
}
