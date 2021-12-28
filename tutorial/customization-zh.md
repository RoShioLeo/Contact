# 自定义明信片款式教程

## 数据包部分

注：若对数据包尚不熟悉，请先阅读 [Wiki](https://minecraft.fandom.com/zh/wiki/%E6%95%B0%E6%8D%AE%E5%8C%85) 内相关内容。

1. 在路径 `data/modid*/postcards` 中，创建一个 JSON 文件。（`modid*` 表示这里可以是任何你喜欢的有效字符串）

   在本教程中，我们在 `data/myself/postcards` 路径下创建文件 `mycard.json`

2. `mycard.json` 文件内容如下

   ```json
    {
        "postcard": {
            "texture": "yourself:mycard",
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
   条目解释：

   ├─ `postcard`：必填项，明信片基础信息

   　　├─ `texture`：明信片材质位置

   　　├─ `width`：明信片宽度（px）

   　　└─ `height`：明信片高度（px）

   ├─ `text`：选填项，可写文字区域信息

   　　├─ `x`：区域左上角横坐标（正轴向右，px）

   　　├─ `y`：区域左上角纵坐标（正轴向下，px）

   　　├─ `width`：区域宽度（px）

   　　├─ `height`：区域高度（px）

   　　└─ `color`：选填项，文字颜色信息，默认为黑色，各参数取值范围为 0~255

   　　　　├─ `alpha`：透明度

   　　　　├─ `red`：红色

   　　　　├─ `green`：绿色

   　　　　└─ `blue`：蓝色

   ├─ `postmark`：选填项，邮戳信息，默认使用模组设定

   　　├─ `texture`：邮戳材质位置

   　　├─ `x`：邮戳左上角横坐标（正轴向右，px）

   　　├─ `y`：邮戳左上角纵坐标（正轴向下，px）

   　　├─ `width`：邮戳宽度（px）

   　　├─ `height`：邮戳高度（px）

   　　└─ `color`：选填项，邮戳染色信息（针对灰度图）

   　　　　├─ `alpha`：透明度

   　　　　├─ `red`：红色

   　　　　├─ `green`：绿色

   　　　　└─ `blue`：蓝色

3. 完成后，将 `data` 文件夹和相应的 `pack.mcmeta` 打包，放入存档文件夹下的 `datapacks` 文件夹，重新进入世界或输入 `/reload` 指令，即可加载数据包。

## 资源包部分

注：若对资源包（旧译材质包）尚不熟悉，请先阅读 [Wiki](https://minecraft.fandom.com/zh/wiki/%E8%B5%84%E6%BA%90%E5%8C%85) 内相关内容。

1. 在完成数据包的添加之后，我们来对明信片的材质部分进行添加。

2. 以上面创建好的 `mycard.json` 文件为例，明信片材质位置上写了 `yourself:mycard`
   。这表明，该明信片的材质位置路径为 `assets/yourself/textures/postcard/mycard.png`。在相应路径上放上文件即可。

3. 在 `mycard.json` 文件中，邮戳材质位置写了 `contact:postmark`，这是本模组自带的邮戳材质，所以不需要额外操作。若使用自己的邮戳材质，如 `ourselves:mark`
   ，则同理，该材质需要放在 `assets/ourselves/textures/postcard/mark.png`。

4. 在添加完明信片之后，不要忘记本地化文件的配置。对于 `mycard.json` 添加的款式而言，其翻译键名为 `tooltip.postcard.myself.mycard`。在相应的本地化文件下操作即可，本教程不再赘述。

5. 完成后，将 `assets` 文件夹和相应的 `pack.mcmeta` 打包，放入 `resourcepacks` 文件夹，进入游戏加载资源包即可。