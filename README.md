# SlideBackLayout
- 使用方法

1. Activity的主题设置为透明

```java
<item name="android:windowIsTranslucent">true</item>
<item name="android:windowBackground">@android:color/transparent</item>
```

2. 在activity的onCreate上调用
```java
SlideBackLayout layout = new SlideBackLayout(this);
layout.bind(this);
```