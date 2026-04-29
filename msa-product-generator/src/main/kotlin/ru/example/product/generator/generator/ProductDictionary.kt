package ru.example.product.generator.generator

import ru.example.product.generator.domain.ProductCategory

/**
 * Static dictionaries containing realistic product attributes for random generation.
 * Contains 50+ items per category across 5 product domains (Electronics, Home, Office, Sports, Fashion).
 */
object ProductDictionary {

    /**
     * Brands organized by product category.
     */
    private val brandsByCategory: Map<ProductCategory, List<String>> = mapOf(
        ProductCategory.ELECTRONICS to listOf(
            "Sony", "Samsung", "Apple", "Bose", "JBL", "Sennheiser", "LG", "Panasonic",
            "Canon", "Toshiba", "Huawei", "Xiaomi", "OnePlus", "ASUS", "Lenovo",
            "Dell", "HP", "Acer", "Microsoft", "Fitbit", "Garmin", "GoPro", "Logitech",
            "Razer", "SteelSeries", "Corsair", "Western Digital", "Kingston", "Nvidia",
            "AMD", "Intel", "Dyson", "Philips", "Braun", "Oster", "Cuisinart", "KitchenAid",
            "Instant Pot", "Ninja", "Keurig", "Whirlpool", "GE", "Frigidaire", "Maytag",
            "Sub-Zero", "Wolf", "Miele", "Breville", "DeLonghi"
        ),
        ProductCategory.CLOTHING to listOf(
            "Zara", "H&M", "Levi's", "Calvin Klein", "Tommy Hilfiger", "Ralph Lauren",
            "Nike", "Adidas", "Under Armour", "Puma", "New Balance", "Reebok",
            "Gap", "Old Navy", "Uniqlo", "Forever 21", "Nordstrom", "Bloomingdale's",
            "Coach", "Michael Kors", "Kate Spade", "Fossil", "Timberland", "Clarks",
            "Dr. Martens", "Converse", "Vans", "Supreme", "Stüssy", "Patagonia",
            "The North Face", "Columbia", "Arc'teryx", "REI", "Lululemon", "Alo Yoga",
            "Spanx", "Victoria's Secret", "Bath & Body Works", "J.Crew", "Banana Republic",
            "Brooks Brothers", "Haggar", "Dockers", "Lee", "Wrangler", "Diesel",
            "Gucci", "Prada", "Versace", "Burberry"
        ),
        ProductCategory.FOOD to listOf(
            "Starbucks", "Dunkin'", "Peet's", "Blue Mountain", "Lavazza", "Illy",
            "Nespresso", "Keurig", "Green Mountain", "Folgers", "Maxwell House",
            "McCafé", "Costa", "Tully's", "Intelligentsia", "Stumptown", "Verve",
            "Counter Culture", "Blue Bottle", "Ritual", "Onyx", "Acme Coffee",
            "La Colombe", "Devil's Advocate", "Health-Ade", "GT's Living Foods",
            "Primal Kitchen", "Principles", "Kettle & Fire", "Vital Proteins", "Gatorade",
            "Powerade", "BodyArmor", "Red Bull", "Monster", "Rockstar", "Celsius",
            "Reign", "Alani Nu", "Ghost", "Ryse", "C4", "Pre-Kaged", "Assault",
            "Optimum Nutrition", "Dymatize", "MuscleTech", "BSN", "Cellucor"
        ),
        ProductCategory.HOME_GOODS to listOf(
            "IKEA", "West Elm", "CB2", "Pottery Barn", "Williams Sonoma", " Crate & Barrel",
            "Restoration Hardware", "Wayfair", "AllModern", "Perigold", "Birch Lane",
            "Tempur-Pedic", "Sleep Number", "Saatva", "Casper", "Purple", "Nectar",
            "Helix", "Tuft & Needle", "Brooklinen", "Parachute", "Brooklyn Bedding",
            "Sheex", "Ettitude", "Boll & Branch", "Malouf", "Utopia Bedding",
            "HomeGoods", "Rugs USA", "Ashley Furniture", "Ashley Homestore",
            "Room & Board", "Design Within Reach", "Muji", "Kartell", "Herman Miller",
            "Steelcase", "Haworth", "Okamura", "Nitori", "Loftmeister", "Eggplant",
            "Kare Design", "Zara Home", "H&M Home", "Monki Home", "& Other Stories Home"
        ),
        ProductCategory.APPLIANCES to listOf(
            "Dyson", "iRobot", "Philips", "Braun", "KitchenAid", "Cuisinart", "Ninja",
            "Instant Pot", "Breville", "DeLonghi", "Whirlpool", "GE", "Frigidaire",
            "Maytag", "Samsung", "LG", "Panasonic", "Toshiba", "Hamilton Beach",
            "Oster", "Crock-Pot", "Black+Decker", "Rival", "Mr. Coffee", "Keurig",
            "Breville", "KitchenAid", "Wolf", "Sub-Zero", "Viking", "Miele",
            "Shark", "Bissell", "Eureka", "Hoover", "Tineco", "Roborock",
            "Ecovacs", "Neato", "iRobot Roomba", "Yeedi", "Viomi", "Xiaomi",
            "Govee", "Philips Hue", "LIFX", "Wemo", "Nanoleaf", "Sengled"
        ),
        ProductCategory.SMART_HOME to listOf(
            "Amazon", "Google", "Apple", "Samsung", "LG", "Philips Hue",
            "LIFX", "Wemo", "Nanoleaf", "Sengled", "Govee", "Ecobee",
            "Nest", "Ring", "Arlo", "Wyze", "Eufy", "Blink", "SimpliSafe",
            "ADT", "Vivint", "August", "Yale", "Schlage", "Kwikset",
            "Sonos", "Bose", "JBL", "Sony", "Samsung", "LG",
            "Roku", "Fire TV", "Apple TV", "Chromecast", "Nvidia Shield",
            "Xiaomi", "Aqara", "Tuya", "SmartThings", "HomeAssistant", "Hubitat",
            "Wink", "Lutron", "Hunter Douglas", "Serena", "Soma"
        ),
    )

    /**
     * Product types organized by category.
     */
    private val productTypesByCategory: Map<ProductCategory, List<String>> = mapOf(
        ProductCategory.ELECTRONICS to listOf(
            "Wireless Headphones", "Bluetooth Speaker", "Smartphone", "Smartwatch", "Wireless Earbuds",
            "Tablet", "Laptop", "Digital Camera", "Action Camera", "Drone",
            "Gaming Console", "Monitor", "Keyboard", "Mouse", "Webcam",
            "External Hard Drive", "USB Hub", "Power Bank", "Charging Cable", "Screen Protector",
            "Phone Case", "Laptop Bag", "Router", "Modem", "Mesh WiFi System",
            "Smart TV", "Soundbar", "Projector", "Streaming Stick", "VR Headset",
            "Graphics Tablet", "Microphone", "Tripod", "Selfie Stick", " Gimbal",
            "E-Reader", "Fitness Tracker", "Portable Charger", "HDMI Cable", "Ethernet Cable",
            "SSD Drive", "RAM Module", "CPU Cooler", "Graphics Card", "Motherboard",
            "Power Supply", "Case Fan"
        ),
        ProductCategory.CLOTHING to listOf(
            "T-Shirt", "Jeans", "Jacket", "Sneakers", "Backpack", "Sunglasses", "Watch",
            "Hoodie", "Polo Shirt", "Dress Shirt", "Blazer", "Chinos",
            "Cargo Pants", "Shorts", "Skirt", "Dress", "Blouse", "Cardigan",
            "Sweater", "Vest", "Coat", "Rain Jacket", "Swimsuit",
            "Boxer Briefs", "Socks", "Scarf", "Gloves", "Beanie",
            "Belt", "Wallet", "Tie", "Bow Tie", "Cufflinks",
            "Boots", "Loafers", "Sandals", "Flip Flops", "Running Shoes",
            "Cross Trainers", "Slip-Ons", "Oxfords", "Derby Shoes", "Monk Straps",
            "Moccasins", "Espadrilles", "Clogs", "Wedge Heels", "Stilettos"
        ),
        ProductCategory.FOOD to listOf(
            "Coffee Beans", "Green Tea", "Protein Powder", "Energy Drink", "Sparkling Water",
            "Granola Bar", "Dried Fruit Mix", "Dark Chocolate", "Olive Oil", "Balsamic Vinegar",
            "Pasta Sauce", "Extra Virgin Olive Oil", "Honey", "Maple Syrup", "Peanut Butter",
            "Almond Butter", "Quinoa", "Chia Seeds", "Coconut Oil", "Apple Cider Vinegar",
            "Hot Sauce", "Soy Sauce", "Worcestershire Sauce", "Mustard", "Mayonnaise",
            "Ketchup", "BBQ Sauce", "Ranch Dressing", "Caesar Dressing", "Italian Dressing",
            "Peanuts", "Cashews", "Almonds", "Walnuts", "Pistachios",
            "Trail Mix", "Beef Jerky", "Protein Bar", "Collagen Peptides", "Multivitamins",
            "Omega-3 Fish Oil", "Probiotics", "Vitamin D", "Turmeric Curcumin", "Melatonin",
            "Pre-Workout", "Creatine", "BCAA", "Mass Gainer"
        ),
        ProductCategory.HOME_GOODS to listOf(
            "Throw Pillow", "Blanket", "Area Rug", "Curtains", "Wall Art",
            "Candle", "Vase", "Picture Frame", "Mirror", "Clock",
            "Door Mat", "Towel Set", "Shower Curtain", "Storage Basket", "Bookshelf",
            "Coffee Table", "Side Table", "Console Table", "TV Stand", "Desk",
            "Office Chair", "Futon", "Daybed", "Ottoman", "Bench",
            "Shoe Rack", "Coat Rack", "Umbrella Stand", "Key Holder", "Mail Organizer",
            "Cutting Board", "Knife Set", "Cookware Set", "Dinnerware Set", "Flatware Set",
            "Glassware Set", "Wine Glasses", "Mugs", "Plates", "Bowls",
            "Baking Sheet", "Mixing Bowls", "Measuring Cups", "Can Opener", "Peeler",
            "Colander", "Grater", "Spatula", "Tongs", "Whisk"
        ),
        ProductCategory.APPLIANCES to listOf(
            "Robot Vacuum", "Air Purifier", "Coffee Maker", "Blender", "Toaster",
            "Iron", "Vacuum Cleaner", "Stand Mixer", "Food Processor", "Slow Cooker",
            "Pressure Cooker", "Air Fryer", "Microwave", "Rice Cooker", "Electric Kettle",
            "Juicer", "Waffle Maker", "Sandwich Maker", "Deep Fryer", "Sous Vide",
            "Dishwasher", "Washing Machine", "Dryer", "Refrigerator", "Freezer",
            "Dish Drying Rack", "Spice Rack", "Pantry Organizer", "Fridge Magnet", "Timer",
            "Humidifier", "Dehumidifier", "Space Heater", "Tower Fan", "Desk Fan",
            "Ceiling Fan", "Portable Heater", "Oil Diffuser", "Night Light", "Smart Plug",
            "Motion Sensor", "Doorbell Camera", "Security Camera", "Smoke Detector", "CO Detector",
            "Thermostat", "Light Bulb", "LED Strip", "Floor Lamp", "Table Lamp"
        ),
        ProductCategory.SMART_HOME to listOf(
            "Smart Speaker", "Smart Display", "Smart Thermostat", "Smart Lock", "Video Doorbell",
            "Security Camera", "Smart Bulb", "Smart Plug", "Smart Switch", "Smart Curtain",
            "Smart Blinds", "Smart Garage Door", "Smart Leak Detector", "Smart Smoke Alarm",
            "Smart Pet Feeder", "Smart Water Fountain", "Smart Plant Monitor", "Smart Garden",
            "Smart Scale", "Smart Mirror", "Smart TV Box", "Streaming Device",
            "Smart Router", "WiFi Extender", "Mesh Network", "Smart Hub", "Smart Gateway",
            "Smart Motion Sensor", "Smart Contact Sensor", "Smart Siren", "Smart Alarm",
            "Smart Window Shade", "Smart Sprinkler", "Smart Pool Controller", "Smart Hot Tub",
            "Smart Fireplace", "Smart Chimney", "Smart Vent", "Smart Damper", "Smart Attic Fan",
            "Smart Whole House Fan", "Smart Window Sensor", "Smart Glass Break Sensor", "Smart Keypad", "Smart Remote"
        ),
    )

    /**
     * Adjectives for product names.
     */
    val adjectives: List<String> = listOf(
        "Premium", "Ultra", "Pro", "Mini", "Portable", "Wireless", "Noise-Cancelling", "Lightweight",
        "Ergonomic", "Waterproof", "Water-Resistant", "Rechargeable", "Fast-Charging", "Bluetooth",
        "Smart", "Digital", "Advanced", "Essential", "Classic", "Deluxe",
        "Professional", "Compact", "Slim", "Heavy-Duty", "Eco-Friendly", "Sustainable",
        "Luxury", "Elite", "Signature", "Limited Edition", "Exclusive", "Premium Plus",
        "High-Performance", "All-in-One", "Multi-Function", "Versatile", "Durable", "Robust",
        "Innovative", "Cutting-Edge", "Next-Gen", "Revolutionary", "Game-Changing", "Industry-Leading",
        "Award-Winning", "Top-Rated", "Best-Selling", "Popular", "Trending", "New Arrival",
        "Organic", "Natural", "Non-Toxic", "BPA-Free", "Hypoallergenic", "Dermatologist-Tested"
    )

    /**
     * Colors for products.
     */
    val colors: List<String> = listOf(
        "Black", "White", "Silver", "Blue", "Red", "Gold", "Navy", "Graphite",
        "Rose Gold", "Forest Green", "Charcoal", "Cream", "Beige", "Brown",
        "Burgundy", "Teal", "Orange", "Yellow", "Purple", "Pink",
        "Khaki", "Olive", "Coral", "Mint", "Lavender", "Copper",
        "Bronze", "Titanium", "Platinum", "Matte Black", "Glossy White", "Translucent",
        "Midnight Blue", "Slate Gray", "Sand", "Ivory", "Champagne", "Ruby",
        "Emerald", "Sapphire", "Amber", "Jade", "Pearl", "Iridescent",
        "Metallic", "Matte", "Glossy", "Textured", "Brushed", "Polished"
    )

    /**
     * Materials for products.
     */
    val materials: List<String> = listOf(
        "Plastic", "Metal", "Rubber", "Fabric", "Leather", "Carbon Fiber",
        "Aluminum", "Titanium", "Stainless Steel", "Silicone", "Canvas", "Cotton",
        "Polyester", "Nylon", "Wool", "Linen", "Suede", "Faux Leather",
        "Wood", "Bamboo", "Glass", "Ceramic", "Porcelain", "Marble",
        "Granite", "Concrete", "Acrylic", "Foam", "Memory Foam", "Latex",
        "Neoprene", "Mesh", "Velvet", "Satin", "Chiffon", "Denim",
        "Tweed", "Felt", "Cork", "Recycled Material", "Biodegradable", "Eco-Friendly Composite"
    )

    /**
     * Tags for products.
     */
    val tags: List<String> = listOf(
        "Bluetooth", "Wi-Fi", "USB-C", "Fast-Charging", "Waterproof", "Noise-Cancelling",
        "Wireless", "Ergonomic", "Portable", "Lightweight", "Rechargeable", "Touch Control",
        "Voice Control", "App-Controlled", "Smart Home Compatible", "Alexa Compatible",
        "Google Home Compatible", "Apple HomeKit", "Matter Supported", "Zigbee",
        "Z-Wave", "Thread", "NFC", "QR Code", "LED Indicator", "OLED Display",
        "LCD Screen", "Touchscreen", "Voice Assistant", "Hands-Free", "Auto-On",
        "Auto-Off", "Energy Efficient", "Low Power", "Solar Powered", "Battery Operated",
        "Wired", "Wireless Charging", "Qi Compatible", "MagSafe", "USB-A", "HDMI",
        "DisplayPort", "Thunderbolt", "Ethernet", "5G", "WiFi 6E", "WiFi 7", "60GHz"
    )

    /**
     * Description templates for generating realistic product descriptions.
     */
    private val descriptionTemplates: List<String> = listOf(
        "Experience premium quality with this exceptional %s. Crafted from high-grade %s, it combines durability with elegant design. Perfect for everyday use.",
        "Elevate your lifestyle with this innovative %s. Featuring advanced %s technology and a sleek %s finish, it delivers outstanding performance.",
        "Discover the perfect blend of style and functionality with this %s. Made with premium %s materials, it offers superior comfort and long-lasting reliability.",
        "This remarkable %s is designed for those who appreciate quality. The %s construction ensures durability while maintaining a lightweight, portable form factor.",
        "Introducing our latest %s, engineered with cutting-edge %s technology. The %s design makes it an ideal choice for modern living.",
        "Transform your daily routine with this versatile %s. Built with premium %s and finished in stunning %s, it combines aesthetics with practicality.",
        "A must-have for any collection, this %s features top-tier %s construction. Its %s appearance complements any environment while delivering exceptional results.",
        "Designed with precision and crafted from the finest %s, this %s represents the perfect fusion of form and function in a beautiful %s colorway."
    )

    /**
     * Category name suffixes for more specific product naming.
     */
    private val categorySuffixes: Map<ProductCategory, List<String>> = mapOf(
        ProductCategory.ELECTRONICS to listOf(
            "Series", "Pro Max", "Lite", "Plus", "Air", "Mini", "Ultra", "Elite",
            "Plus", "Max", "Slim", "Compact", "Advanced", "Next Gen", "2nd Gen", "3rd Gen"
        ),
        ProductCategory.CLOTHING to listOf(
            "Classic Fit", "Slim Fit", "Regular Fit", "Relaxed Fit", "Tailored", "Oversized",
            "Cropped", "Longline", "Zip-Up", "Pullover", "Button-Down", "V-Neck",
            " crew-Neck", "Henley", "Turtleneck", "Mock Neck", "Polo", "Raglan"
        ),
        ProductCategory.FOOD to listOf(
            "Organic", "Raw", "Unroasted", "Single-Origin", "Fair Trade", "Fair-Trade",
            "Non-GMO", "Gluten-Free", "Sugar-Free", "Low-Sugar", "High-Protein", "Low-Fat",
            "Full-Fat", "Extra Strength", "Regular Strength", "Mild", "Medium", "Hot", "Extra Hot"
        ),
        ProductCategory.HOME_GOODS to listOf(
            "Set of 2", "Set of 4", "Set of 6", "Set of 12", "Pack of 3", "Pack of 6",
            "Queen Size", "King Size", "Twin Size", "Full Size", "Standard", "Large",
            "Extra Large", "Compact", "Oversized", "Round", "Square", "Rectangular"
        ),
        ProductCategory.APPLIANCES to listOf(
            "Digital", "Automatic", "Manual", "Programmable", "Timer", "Adjustable",
            "Variable Speed", "One-Touch", "Push-Button", "Touchscreen", "LCD Display", "LED Display",
            "Stainless Steel", "Brushed Nickel", "Retro", "Modern", "Traditional", "Contemporary"
        ),
        ProductCategory.SMART_HOME to listOf(
            "Kit", "Starter Kit", "Starter Pack", "Bundle", "Hub", "Gateway", "Controller",
            "System", "Starter System", "Pro Kit", "Advanced Kit", "Complete Kit",
            "Indoor", "Outdoor", "Indoor/Outdoor", "Weatherproof", "Weather-Resistant", "Waterproof"
        ),
    )

    /**
     * Get a random brand for the given category.
     */
    fun getRandomBrand(category: ProductCategory): String {
        val brands = brandsByCategory[category] ?: brandsByCategory[ProductCategory.ELECTRONICS]!!
        return brands.random()
    }

    /**
     * Get a random product type for the given category.
     */
    fun getProductType(category: ProductCategory): String {
        val types = productTypesByCategory[category] ?: productTypesByCategory[ProductCategory.ELECTRONICS]!!
        return types.random()
    }

    /**
     * Get a random adjective.
     */
    fun getRandomAdjective(): String {
        return adjectives.random()
    }

    /**
     * Get a random color.
     */
    fun getRandomColor(): String {
        return colors.random()
    }

    /**
     * Get a random material.
     */
    fun getRandomMaterial(): String {
        return materials.random()
    }

    /**
     * Get random unique tags (1 to maxCount).
     */
    fun getRandomTags(maxCount: Int = 5): List<String> {
        val count = (1..minOf(maxCount, tags.size)).random()
        return tags.shuffled().take(count)
    }

    /**
     * Get a random category suffix.
     */
    fun getRandomCategorySuffix(category: ProductCategory): String {
        val suffixes = categorySuffixes[category] ?: categorySuffixes[ProductCategory.ELECTRONICS]!!
        return suffixes.random()
    }

    /**
     * Generate a random description using templates.
     */
    fun generateDescription(productType: String, material: String, color: String): String {
        val template = descriptionTemplates.random()
        return template.format(productType, material.lowercase(), color.lowercase())
    }

    /**
     * Get all available categories.
     */
    fun getAllCategories(): List<ProductCategory> {
        return ProductCategory.values().toList()
    }

    /**
     * Get brands for a specific category.
     */
    fun getBrandsForCategory(category: ProductCategory): List<String> {
        return brandsByCategory[category] ?: emptyList()
    }

    /**
     * Get product types for a specific category.
     */
    fun getProductTypesForCategory(category: ProductCategory): List<String> {
        return productTypesByCategory[category] ?: emptyList()
    }
}
