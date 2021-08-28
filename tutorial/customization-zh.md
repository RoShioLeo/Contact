# 自定义明信片款式教程

1. 在数据包路径 `data/modid/postcard` 中，创建一个 JSON 文件。

   例如，我们先在 `data/contact/postcard` 创建文件 `creeper.json`

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

   `postcard` 为必填项。`text`  `postmark` 和 `color` 为可选项，一旦写上，请务必将子项写完整。

   如果 `texture` 项为 `contact:creeper` 你需要将 `assets/contact/textures/postcard/creeper.png`

   `color` 各项的数值范围为 `0` 到 `255`.

3. 最后不要忘记给你的明信片款式加上本地化文件。对于 `creeper.json`，翻译键名为 `tooltip.postcard.contact.creeper`