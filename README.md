# SlideBackLayout
- 使用方法

1. Activity的主题设置为透明

```java
<item name="android:windowIsTranslucent">true</item>
<item name="android:windowBackground">@android:color/transparent</item>
```

2. 使用方式
  *  方式1：在activity的onCreate上调用
```java
SlideBackLayout layout = new SlideBackLayout(this);
layout.bind(this);
```
  * 方式2：在布局中使用，使用该方式，最好把actionBar隐藏，布局中使用ToolBar，因为设置了透明，动画失效，需要代码再次设置覆盖关闭动画。
  ```java
  //在布局的最外层使用SlideBackLayout，找到控件后调用setOnSlideListener()设置监听，在回调上做操作
  SlideBackLayout layout = findViewById(R.id.slideLayout);
  layout.setOnSlideListener(new SlideBackLayout.OnSlideListener() {
      @Override
      public void onSlideClose() {
          finish();
      }
  });
  ```

  3. gradle使用

  - compile 'com.hzh:slide-back-layout:1.0.3'