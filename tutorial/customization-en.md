# Postcard Style Customization Tutorial

## Data Pack Part

Note: If you're not familiar with data pack, please read the [Wiki](https://minecraft.fandom.com/wiki/Data_pack) first.

1. In data pack directory `data/modid*/postcards`, create a new JSON file. (`modid*` means any valid word you like)

   For example, in this tutorial, we will create the file `mycard.json`, in `data/myself/postcards`.

2. The file `mycard.json` content will be as shown below

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

    Item explanation:

    ├─ `postcard`: Mandatory, postcard basic information

    　　├─ `texture`: Location of postcard texture

    　　├─ `width`: Width of postcard (px)

    　　└─ `height`: Height of postcard (px)

    ├─ `text`: Optional, text area information

    　　├─ `x`: Abscissa of upper left corner (Positive direction is right. px)

    　　├─ `y`: Ordinate of upper left corner (Positive direction is down. px)

    　　├─ `width`: Area width (px)

    　　├─ `height`: Area height (px)

    　　└─ `color`: Optional, text color information. Default is black. Value range is 0~255

    　　　　├─ `alpha`: Opacity

    　　　　├─ `red`: Red

    　　　　├─ `green`: Green

    　　　　└─ `blue`: Blue

    ├─ `postmark`: Optional, postmark information.

    　　├─ `texture`: Location of postmark texture

    　　├─ `x`: Abscissa of upper left corner (Positive direction is right. px)

    　　├─ `y`: Ordinate of upper left corner (Positive direction is down. px)

    　　├─ `width`: Postmark width (px)

    　　├─ `height`: Postmark height (px)

    　　└─ `color`: Optional, postmark dyeing information. (Only for grayscale image)

    　　　　├─ `alpha`: Opacity

    　　　　├─ `red`: Red

    　　　　├─ `green`: Green

    　　　　└─ `blue`: Blue

3. Then, pack `data` folder and `pack.mcmeta`. Put it into `datapacks` folder in your saves directory. Reenter the world or use the command `/reload` to load the data pack.

## Resource Pack Part

Note: If you're not familiar with resource pack (texture pack in old), please read the [Wiki](https://minecraft.fandom.com/wiki/Resource_Pack) first.

1. Now we need to add textures of postcard.
2. Take the file `mycard.json` created above as an example, the location of postcard texture is `yourself:mycard`. It means you need to put the picture in the directory `assets/yourself/textures/postcard/mycard.png`.
3. And the location of postmark texture is `contact:postmark`. This is the default postmark. So there is no more you need to do. If you want to use your own postmark, such as `ourselves:mark`, by the same token, you need to put the picture in the directory `assets/ourselves/textures/postcard/mark.png`.
4. Don't forget to add translation keys of style in the lang file. For `mycard.json`, the key
   was `tooltip.postcard.myself.mycard`.
5. Then, pack `assets` folder and `pack.mcmeta`. Put it into `resourcepacks` folder and load it.
6. Now you can find a new postcard in creative inventory.

