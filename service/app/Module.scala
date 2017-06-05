import com.google.inject.AbstractModule
import db.MySqlUserRepository
import play.api.libs.concurrent.AkkaGuiceSupport
import repositories.UserRepository

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MySqlUserRepository])
  }
}
