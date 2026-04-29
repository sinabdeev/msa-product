package ru.example.product.generator.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Product API")
                    .description(
                        """
                        Product management API for msa-product-generator.
                        
                        This API provides CRUD operations for products with the following features:
                        - Create, read, update, and delete products
                        - Validation of product data
                        - Standardized error responses
                        - OpenAPI 3.0 documentation
                        
                        ### Product Fields
                        - **sku**: Stock keeping unit (unique identifier)
                        - **name**: Product name
                        - **description**: Product description
                        - **price**: Price in currency (BigDecimal)
                        - **quantity**: Quantity in stock
                        - **weight**: Weight in kilograms (optional)
                        - **isAvailable**: Availability flag
                        - **category**: Product category (ELECTRONICS, CLOTHING, FOOD, HOME_GOODS)
                        - **tags**: List of tags
                        - **createdAt**: Creation timestamp
                        - **updatedAt**: Last update timestamp
                        """.trimIndent(),
                    )
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("Product Team")
                            .email("product@example.com"),
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html"),
                    ),
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080")
                        .description("Local development server"),
                    Server()
                        .url("https://api.example.com")
                        .description("Production server"),
                ),
            )
    }
}
