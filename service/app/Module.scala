import com.google.inject.AbstractModule
import db.{MySqlBuyerCategoryRepository, MySqlConsumptionThresholdRepository, MySqlProductRepository, MySqlUserRepository}
import play.api.libs.concurrent.AkkaGuiceSupport
import repositories.{BuyerCategoryRepository, ConsumptionThresholdRepository, ProductRepository, UserRepository}

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MySqlUserRepository])
    bind(classOf[ProductRepository]).to(classOf[MySqlProductRepository])
    bind(classOf[BuyerCategoryRepository]).to(classOf[MySqlBuyerCategoryRepository])
    bind(classOf[ConsumptionThresholdRepository]).to(classOf[MySqlConsumptionThresholdRepository])
  }
}
