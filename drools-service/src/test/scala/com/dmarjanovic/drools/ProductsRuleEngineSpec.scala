package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain.Product
import org.scalatest.{MustMatchers, WordSpecLike}

class ProductsRuleEngineSpec extends WordSpecLike with MustMatchers {

  "Products Rule Engine" when {
    "determining products we need to fill stock" should {
      "change all products that have smaller quantity than allowed" in new ProductsFixture {
        val products: List[Product] = (1 until 10).map(seed => createProduct(seed, seed ^ 2, seed)).toList

        // Check if every product is set to 'false' before rules triggering
        products.foreach(product =>
          product.fillStock must be(false)
        )

        // Trigger rules
        RulesEngine.determineProductWeNeedToFillStock(products)

        // Check if products with quantity < minQuantity has 'true' flag
        products.foreach(product =>
          if (product.quantity < product.minQuantity)
            product.fillStock must be(true)
          else
            product.fillStock must be(false)
        )
      }
    }
  }

}
