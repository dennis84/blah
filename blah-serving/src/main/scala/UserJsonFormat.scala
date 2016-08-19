package blah.serving

trait UserJsonFormat extends ServingJsonProtocol {
  implicit val userQueryFmt = jsonFormat2(UserQuery)
  implicit val userEventFmt = jsonFormat5(UserEvent)
  implicit val userFmt = jsonFormat13(User)
  implicit val userCountFmt = jsonFormat2(UserCount)
}
