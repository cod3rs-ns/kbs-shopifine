package com.dmarjanovic.drools.hateoas

case class CollectionLinks(prev: Option[String] = None, self: String, next: Option[String] = None)
