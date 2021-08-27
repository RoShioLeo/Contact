**Postcard Style Customization Tutorial**

1. Create a new json file in datapack `data/modid/postcard`.

   For example, we create `creeper.json` in `data/contact/postcard`

2. ```json
    {
        "postcard": {
            "texture": "contact:creeper",
            "width": 200,
            "height": 133
        },
        "text": {
            "x": 10,
            "y": 14,
            "width": 180,
            "height": 108,
            "color": {
                "alpha": 255,
                "red": 29,
                "green": 149,
                "blue": 63
            }
        },
        "postmark": {
            "texture": "contact:postmark",
            "x": 142,
            "y": -5,
            "width": 64,
            "height": 52,
            "color": {
                "alpha": 120,
                "red": 127,
                "green": 184,
                "blue": 14
            }
        }
    }
    ```

   `postcard` is mandatory. `text`  `postmark` and `color` is optional, but if you write one of them, please to write
   completely.

   if `texture` is `contact:creeper`, you need to put in the path `assets/contact/textures/postcard/creeper.png`

   `color` is from `0` to `255`.

3. Don't forget to create translation key of style in the lang file. For `creeper.json`, the key
   was `tooltip.postcard.contact.creeper`