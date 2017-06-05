import com.google.inject.AbstractModule
import db.{MySqlProductRepository, MySqlUserRepository}
import play.api.libs.concurrent.AkkaGuiceSupport
import repositories.{ProductRepository, UserRepository}

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MySqlUserRepository])
    bind(classOf[ProductRepository]).to(classOf[MySqlProductRepository])
  }
}
