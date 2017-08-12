package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.ConsumptionThreshold

case class ConsumptionThresholdAttributes(from: Int, to: Int, award: Double)

case class ConsumptionThresholdRelationships(category: ResponseRelationship)

case class ConsumptionThresholdData(`type`: String, id: Long, attributes: ConsumptionThresholdAttributes, relationships: ConsumptionThresholdRelationships) {
  def toDomain: ConsumptionThreshold = {
    ConsumptionThreshold(
      from = attributes.from,
      to = attributes.to,
      award = attributes.award
    )
  }
}

case class ConsumptionThresholdCollectionResponse(data: Seq[ConsumptionThresholdData], links: CollectionLinks)
