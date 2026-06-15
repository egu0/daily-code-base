
# 移动端（第二部分）

## 响应式布局

本章要点

1. 理解响应式开发
2. 了解 Bootstrep 前端开发框架
3. 了解 Bootstrap 栅格系统
4. 阿里百秀首页案例

### 响应式开发原理

原理：使用**媒体查询**针对不同宽度的设备进行布局和样式的设置，从而适配不同设备。

按照宽度进行屏幕分类划分：

| 设备划分                     | 尺寸空间             |
| ---------------------------- | -------------------- |
| 超小屏幕（比如手机）         | `w < 768px`          |
| 小屏设备（比如平板）         | `768px<= w < 992px`  |
| 中等屏幕（比如桌面显示器）   | `992px<= w < 1200px` |
| 宽屏设备（比如大桌面显示器） | `x >= 1200px`        |

### 响应式布局容器

响应式需要一个**父级元素**做**布局容器**，用它来配合子级元素来实现变化效果。

原理：**先通过媒体查询来改变这个父级容器的大小，再改变里面子级元素的排列方式和大小，从而实现不同屏幕下不同的页面布局和样式变化**

**常用的不同设备划分下的容器宽度**（在哪种屏幕尺寸下占屏幕多宽）

|          | 超小屏幕 (<768px) | 小屏设备(≥768px) | 中等屏幕(≥992px) | 宽屏设备(≥1200px) |
| -------- | ----------------- | ---------------- | ---------------- | ----------------- |
| 容器宽度 | 100%              | 750px            | 970px            | 1170px            |

示例

```html
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <style>
      /* 手机尺寸 */
      @media screen and (max-width: 768px) {
        .container {
          width: 100%;
        }
      }
      /* 平板屏幕 */
      @media screen and (min-width: 768px) {
        .container {
          width: 750px;
        }
      }
      /* 中等屏幕 */
      @media screen and (min-width: 992px) {
        .container {
          width: 970px;
        }
      }
      /* 大屏幕 */
      @media screen and (min-width: 1200px) {
        .container {
          width: 1170px;
        }
      }
      .container {
        margin: 0 auto;
        width: 200px;
        background-color: pink;
      }
    </style>
  </head>
  <body>
    <div class="container">123</div>
  </body>
```

示例：响应式布局下的导航栏案例

1. 当屏幕宽度大于等于 768px 时设置布局容器宽度为 750px，此时容器中包含 8 个 li 元素，每个 li 元素宽度为 750px / 8 = 93.75px，高度为 30px，浮动一行显示
2. 当屏幕宽度小于 768px 时设置布局容器宽度为 100%，此时 8 个 li 元素宽度为 33.33%，每行显示 3 个 li 元素

```html
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style>
      ul {
        padding: 0;
        margin: 0;
      }
      li {
        list-style: none;
      }
      .container {
        background-color: pink;
        margin: 0 auto;
        height: 100px;
      }
      .container ul li {
        float: left;
        box-sizing: border-box;
        height: 30px;
        border: 1px solid #000;
      }
      /* 屏幕宽度 <768 */
      @media screen and (max-width: 768px) {
        .container {
          width: 100%;
        }
        .container ul li {
          width: 33.33%;
        }
      }
      /* 屏幕宽度 >=768（利用层叠性，上边设置的【等于 768px】条件被覆盖！） */
      @media screen and (min-width: 768px) {
        .container {
          width: 750px;
        }
        .container ul li {
          width: 93.75px;
        }
      }
    </style>
    <title>Document</title>
  </head>
  <body>
    <div class="container">
      <ul>
        <li>1</li>
        <li>2</li>
        <li>3</li>
        <li>4</li>
        <li>5</li>
        <li>6</li>
        <li>7</li>
        <li>8</li>
      </ul>
    </div>
  </body>
</html>
```

### Bootstrap 框架简介

官网：[Bootstrap v3.4](https://getbootstrap.com/docs/3.4/)

介绍：Bootstrap is the most popular HTML, CSS, and JS framework for developing responsive, mobile first projects on the web.

优点：

- 标准化的 html + css 编码规范
- 提供了一套简洁、直观、强悍的组件
- 有自己的生态圈，不断的更新迭代
- 让开发更简单，提高了开发效率

### Bootstrap 使用

步骤：

1、下载 bootstrap 静态资源，将其拷贝到项目

2、创建 html 骨架结构

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <!-- 要求当前网页使用 IE 浏览器最高版本的内核来渲染 -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <!-- 视口设置：视口的宽度和设备一致，默认的缩放比例和 PC 端一致，用户不能自行缩放 -->
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Bootstrap 101 Template</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://cdn.jsdelivr.net/npm/html5shiv@3.7.3/dist/html5shiv.min.js"></script>
      <script src="https://cdn.jsdelivr.net/npm/respond.js@1.4.2/dest/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <h1>Hello, world!</h1>
  </body>
</html>
```

3、引入相关样式（见 2）

4、书写内容

### Bootstrap 布局容器

Bootstrap 定义了两个布局容器的类：`.container` 和 `.container-fluid`，开发者可以直接使用。

- `.container`：响应式布局容器类，容器宽度在不同设备下不同：超小屏（<768px）：宽度为 100%；小屏（>=768px）：宽度为 750px；中屏（>=992px）：宽度为 970px；大屏（>=1200px）：宽度为 1170px

- `.container-fluid`：流式布局容器类，占百分百宽度，适合于单独制作移动端页面

### Bootstrap 栅格系统

栅格系统（grid system，也叫网格系统），指将页面布局划分为**等宽的列**，然后通过**列数**的定义来模块化页面布局。

Bootstrap 提供了一套响应式、移动设备优先的栅格系统，系统会自动将屏幕（或视口 viewport）尺寸划分为 12 份。

栅格系统使用一系列的行（row）和列（column）的组合来划分页面，页面内容可以放入划分好的格子中。

|          | 超小屏幕 (<768px) | 小屏设备(≥768px) | 中等屏幕(≥992px) | 宽屏设备(≥1200px) |
| -------- | ----------------- | ---------------- | ---------------- | ----------------- |
| 容器宽度 | 100%              | 750px            | 970px            | 1170px            |
| 列前缀   | `.col-xs-`        | `.col-sm-`       | `.col-md-`       | `.col-lg-`        |

注意点：

- 行（用 `row` 属性修饰）必须作为 `container` 元素的子元素
- 列有前缀修饰，用于区分哪种设备下的列：`col-xs` 是 extra small、`col-sm` 是 small、`col-middle` 是 middle、`col-lg` 是 large
- 每一行包含 12 列，默认靠左排列
- 每一列默认包含长度为 15px 的左右 padding
- 当每一行的总列数超过 12 时，多余的部分会被放在下一行
- 可以为同一个列指定多个设备的类名，以便划分不同分数，例如 `class="col-lg-4 col-sm-6"` 表示对应的元素在宽屏设备上占 4 列、在小屏设备上占 6 列

示例

```html
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Bootstrap 101 Template</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css" />
    <!--[if lt IE 9]>
      <script src="https://cdn.jsdelivr.net/npm/html5shiv@3.7.3/dist/html5shiv.min.js"></script>
      <script src="https://cdn.jsdelivr.net/npm/respond.js@1.4.2/dest/respond.min.js"></script>
    <![endif]-->
    <style>
      [class^='col-lg-'] {
        height: 80px;
        box-sizing: border-box;
        border: 1px solid #ccc;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="col-lg-3">1</div>
        <div class="col-lg-3">2</div>
        <div class="col-lg-3">3</div>
        <div class="col-lg-3">4</div>
      </div>
      <div class="row">
        <div class="col-lg-6">1</div>
        <div class="col-lg-2">2</div>
        <div class="col-lg-2">3</div>
        <div class="col-lg-2">4</div>
      </div>
      <!-- 一行的总列数小于 12 -->
      <div class="row">
        <div class="col-lg-6">1</div>
        <div class="col-lg-2">2</div>
        <div class="col-lg-2">3</div>
        <div class="col-lg-1">4</div>
      </div>
      <!-- 一行的总列数大于 12 -->
      <div class="row">
        <div class="col-lg-6">1</div>
        <div class="col-lg-2">2</div>
        <div class="col-lg-2">3</div>
        <div class="col-lg-3">4</div>
      </div>
    </div>
  </body>
</html>
```

示例，需求：

- 在宽屏设备下，每一行放 4 个盒子
- 在中等屏幕下，每一行放 3 个盒子
- 在小屏幕下，每一行放 2 个盒子
- 在超小屏幕下，每一行放 1 个盒子

```html
<body>
  <div class="container">
    <div class="row">
      <div class="col-lg-3 col-md-4 col-sm-6 col-sm-12">1</div>
      <div class="col-lg-3 col-md-4 col-sm-6 col-sm-12">2</div>
      <div class="col-lg-3 col-md-4 col-sm-6 col-sm-12">3</div>
      <div class="col-lg-3 col-md-4 col-sm-6 col-sm-12">4</div>
    </div>
  </div>
</body>
```

⭐ 补充：栅格系统中的**列嵌套**

```html
<body>
  <div class="container">
    <div class="row">
      <div class="col-lg-4">
        <!-- 将某一列看成一个单独的行，可以继续分 -->
        <div class="row">
          <div class="col-lg-4">a</div>
          <div class="col-lg-8">b</div>
        </div>
      </div>
      
      <div class="col-lg-4">2</div>
      <div class="col-lg-4">3</div>
    </div>
  </div>
</body>
```

![image-20240810193316579](./README.assets/image-20240810193316579.png)

⭐ 补充：栅格系统中的**列偏移**

示例：左右对齐

```html
<body>
  <div class="container">
    <div class="row">
      <div class="col-lg-2">1</div>
      <!-- 8 = 12 - 2 - 2 -->
      <div class="col-lg-2 col-lg-offset-8">2</div>
    </div>
  </div>
</body>
```

![image-20240810193529229](./README.assets/image-20240810193529229.png)

示例：中间对齐

```html
<body>
  <div class="container">
    <div class="row">
      <div class="col-lg-8 col-lg-offset-2">center</div>
    </div>
  </div>
</body>
```

![image-20240810193705845](./README.assets/image-20240810193705845.png)

⭐ 补充：栅格系统中的**列排序**

- `col-lg-push-份` 表示往右移动指定份数
- `col-lg-pull-份` 表示往左移动指定份数

```html
<body>
  <div class="container">
    <div class="row">
      <div class="col-lg-4 col-lg-push-8">right</div>
      <div class="col-lg-8 col-lg-pull-4">left</div>
    </div>
  </div>
</body>
```

![image-20240810194419359](./README.assets/image-20240810194419359.png)

⭐ 补充：栅格系统中的**显示与隐藏工具**

在指定设备下**隐藏列或列中的元素**：使用类 `.hidden-lg / .hidden-md / .hidden-sm / .hidden-xs`

示例：在 md、sm、xs 设备下隐藏列

```html
  <body>
    <div class="container">
      <div class="row">
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">1</div>
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">2</div>
        <!-- 在 md、sm、xs 设备下不显示第三列，在 lg 设备下才显示 -->
        <div class="col-lg-3 hidden-md hidden-sm hidden-xs">
          3（发生什么事了）
        </div>
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">4</div>
      </div>
    </div>
  </body>
```

示例：在 lg 设备下隐藏元素

```html
  <body>
    <div class="container">
      <div class="row">
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">1</div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">2</div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
          <!-- 在 lg 设备下隐藏此元素 -->
          <span class="hidden-lg">3</span>
        </div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">4</div>
      </div>
    </div>
  </body>
```

在指定设备下**显示列或列中的元素**：使用 `visible-lg / visible-md / visible-sm / visible-xs`

示例：某行共有四列，仅在 lg 设备下展示这四列中的 `.show` 元素

```html
  <body>
    <div class="container">
      <div class="row">
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">1</div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">2</div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">3</div>
        <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
          <!-- 在 lg 设备下显示“呱呱”，在其他设备下显示 4 -->
          <span class="hidden-lg">4</span>
          <span class="visible-lg" style="font-size: 30px">呱呱～～～</span>
        </div>
      </div>
    </div>
  </body>
```

示例：在 lg 设置下才显示第四列

```html
  <body>
    <div class="container">
      <div class="row">
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">1</div>
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">2</div>
        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-4">3</div>
        <div class="visible-lg col-lg-3">4</div>
      </div>
    </div>
  </body>
```

### 综合案例-阿里百秀 ⭐

方案：响应式页面开发

技术：bootstrap 框架

设计图：采用 1280px 设计尺寸

尺寸等级分为：超小、小(>=768px)、中等(>=992px)、大(>=1200px)、超大(>=1280px)

布局策略：先布局中等屏幕以上的 pc 端布局，再根据实际需求修改小屏幕和超小屏幕的特殊布局样式

步骤：

1. 搭建项目框架
2. 根据设计稿修改 `.container` 容器的响应规则，在屏幕宽度大于等于 1280px 时容器宽度为 1280px
3. 左侧 logo 区域制作
4. 左侧导航 nav 区域制作
5. 中间新闻模块 news 布局和第一个盒子制作
6. 中间新闻模块 news 剩下部分制作
7. 中间发布模块 publish 制作
8. 右侧区域制作
9. 左侧 logo 区域适配 xs 和 sm 屏幕
10. 左侧 nav 区域适配 xs 和 sm 屏幕
11. 中间 news 区域适配 xs 屏幕
12. 中间 publish 区域适配 xs 屏幕以及其他调整

## vw/vh 布局

### vw/vh 布局介绍

vw 和 vh 都是相视口尺寸的单位。vw 表示 viewport width 是相对视口宽度的单位，vh 表示 viewport height 是相对于视口高度的单位。

- 1vw 表示视口的**百分之一**。`1vw = 1/100 viewport width`
- 1vh 表示视口的**百分之一**。`1vh = 1/100 viewport height`

计算示例：某盒子宽度为 68px，在尺寸为 375px 的设备下，该盒子占 vw 值是多少？

```text
1.将屏幕宽度分为 100 份，一份的长度是 1vw，则有
375px / 100vh = 3.75px/vh

2.做除法可得盒子的 vw 值
68px / (3.75px/vw) = 68/3.75vw
```

vw 和 rem 比较像，rem 布局中是将屏幕尺寸分 10 份做为相对单位（也就是 html 的字体大小），而 vw 是将屏幕尺寸分 100 份做为单位

采用 vw/vh 方案可以取代之前的 flexible.js 的作用

在开发过程中，常使用 vw 单位，很少用 vh 单位

注意点：百分比是相对于父盒子来说的，而 vw/vh 是相对于视口来说的

示例

```html
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <style>
      div {
        background-color: pink;
        /* 100px / 3.75px/vh = 26.67 */
        width: 26.667vw;
        height: 26.667vw;
        /* 25 / 3.75 = 6.667 */
        font-size: 6.667vw;
      }
    </style>
  </head>
  <body>
    <div>apple</div>
  </body>
</html>
```

### 综合案例-bilibili首页

方案：单独制作移动端页面

技术：vw 单位 + less

设计图：采用 375px 设计尺寸

步骤：

1、项目准备 + 头部 logo 制作

2、头部右侧 right 制作

3、频道模块布局和 after 区域制作

4、频道模块 tabs 区域制作 —— 无限列表

5、频道模块 tabs 区域制作 —— 当前 tab 页小短线

6、主体区域 m-home 布局

7、主体区域中视频项目 video-item 布局

8、主体区域中视频项目 video-item 中的 count 盒子内容制作

9、主体区域中视频项目 video-item 中的 title 盒子内容制作

10、底部固定定位盒子制作

### vmin/vmax 单位

vmin 和 vmax 是与当下屏幕的宽度和高度的最大化值或最小值有关，取决于哪个更大或更小

vmin = min(viewport-width, viewport-height), vmax 同理

使用 vmin 单位可以让手机端对横屏和竖屏的显示效果更好

示例

```html
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <style>
      div {
        width: 26.67vmin;
        height: 26.67vmin;
        font-size: 5.86vmin;
        background-color: pink;
      }
    </style>
  </head>
  <body>
    <div>123</div>
  </body>
```
