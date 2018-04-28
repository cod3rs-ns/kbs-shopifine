package ws

import domain.BillState
import play.api.libs.json._
import ws.Messages.BillStateChanged

import scala.reflect.ClassTag

object MessageFormat {

  implicit val billState: Format[BillState] = javaEnumFormat[BillState]

  implicit val billStateChanged: OFormat[BillStateChanged] =
    Json.format[BillStateChanged]

  def javaEnumFormat[E <: Enum[E]: ClassTag]: Format[E] = new Format[E] {
    override def reads(json: JsValue): JsResult[E] =
      json.validate[String] match {
        case JsSuccess(value, _) =>
          try {
            val clazz =
              implicitly[ClassTag[E]].runtimeClass.asInstanceOf[Class[E]]
            JsSuccess(Enum.valueOf(clazz, value))
          } catch {
            case _: IllegalArgumentException =>
              JsError("enumeration.unknown.value")
          }
        case JsError(_) => JsError("enumeration.expected.string")
      }

    override def writes(o: E): JsValue = JsString(o.toString)
  }
}
