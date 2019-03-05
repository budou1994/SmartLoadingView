##### 项目介绍
   > 在审核某些问题的时候会出现加载进度展示的view。给用户一个感官上的效果，增强了APP与用户之间的交互体验。自定义审核过渡页纯粹就只是一个自定义view，支持展示文字以及展示图片和进度加载的更换。也方便了大家的使用吧。
##### 效果展示：
![result.gif](https://upload-images.jianshu.io/upload_images/7542553-5457e7cdd28a3a47.gif?imageMogr2/auto-orient/strip)
##### 功能实现
  1.**自定义属性**
  
```
  <declare-styleable name="SmartLoadingView">
        <!-- 是否开启抗锯齿 -->
        <attr name="antiAlias" format="boolean" />
        <!--整个view背景-->
        <attr name="allBack" format="reference|color" />
        <!--分割线上面部分进度条以及提示字-->
        <!--进度条中间的图片-->
        <attr name="circleBitmap" format="reference" />
        <!--展示icon的宽度-->
        <attr name="circleBitmapWidth" format="dimension" />
        <!--展示icon的高度-->
        <attr name="circleBitmapHeight" format="dimension" />
        <!--是否展示中间的图片-->
        <attr name="isShowIcon" format="boolean" />
        <!--进度条和图片之间的距离-->
        <attr name="circlePadding" format="dimension" />
        <!--进度条的宽度-->
        <attr name="circleWidth" format="dimension" />
        <!--进度条距离外部周围的距离-->
        <attr name="circleMargin" format="dimension" />
        <!--进度条的背景颜色-->
        <attr name="circleBgColor" format="color|reference" />
        <!--进度条的循环颜色-->
        <attr name="circleColor" format="color|reference" />
        <!--进度条走完一圈需要时间-->
        <attr name="circleShowTime" format="integer" />
        <!--进度条终止展示角度-->
        <attr name="circleEndAngle" format="float" />
        <!--进度条循环起始角度-->
        <attr name="circleStartAngle" format="float" />
        <!--引导页提示内容-->
        <attr name="hint" format="string" />
        <!--引导提示字颜色-->
        <attr name="hintColor" format="color|reference" />
        <!--引导提示字大小-->
        <attr name="hintSize" format="dimension" />
        <!--引导提示字是否展示-->
        <attr name="hintIsShow" format="boolean" />
        <!--分割线-->
        <!--分割线高度-->
        <attr name="lineHeight" format="dimension" />
        <!--分割线的颜色-->
        <attr name="lineColor" format="color|reference" />
        <!--分割线上下的间距-->
        <attr name="lineMargin" format="dimension" />
        <!--底部内容-->
        <!--未选中icon-->
        <attr name="bottomUnSelectIcon" format="reference" />
        <!--选中的的icon-->
        <attr name="bottomSelectIcon" format="reference" />
        <!--内容icon的宽度-->
        <attr name="bottomIconWidth" format="dimension" />
        <!--内容icon的高度-->
        <attr name="bottomIconHeight" format="dimension" />
        <!--默认内容展示颜色-->
        <attr name="defaultColor" format="color" />
        <!--选中内容展示颜色-->
        <attr name="chooseColor" format="color" />
        <!--内容标题字体颜色-->
        <attr name="titleColor" format="color" />
        <!--展示标题集合-->
        <attr name="titles" format="reference" />
        <!--默认展示内容集合-->
        <attr name="contentUnSelect" format="reference" />
        <!--选中展示内容集合-->
        <attr name="contentSelect" format="reference" />
        <!--标题字体大小-->
        <attr name="titleSize" format="dimension" />
        <!--内容字体大小-->
        <attr name="contentSize" format="dimension" />
        <!--连接线宽度-->
       <attr name="connectLineWidth" format="dimension"/>
    </declare-styleable>

```
  2.**在view中绘制对应view**
* 绘制进度条
```
 private void drawProgress(Canvas canvas) {
        if (isShowIcon) {
            //创建指定大小的bitmap对象
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), circleBitmap);
            canvas.drawBitmap(bitmap, null, new RectF(width / 2 - circleBitmapWidth / 2,
                    (int) (circlePadding + circleMargin), (int) width / 2 + circleBitmapWidth / 2,
                    (int) (circlePadding + circleMargin) + circleBitmapHeight), bitmapPaint);
            bitmap.recycle();
            bitmap = null;
        }
        //创建指定大小的circle元
        canvas.drawCircle(width / 2, circlePadding + circleBitmapHeight / 2 + circleMargin, circleBitmapHeight / 2 + circlePadding, circleBgPaint);
        canvas.drawArc(width / 2 - circleBitmapWidth / 2 - circlePadding,
                circleMargin,
                width / 2 + circleBitmapWidth / 2 + circlePadding
                , circleBitmapHeight + circlePadding + circlePadding + circleMargin,
                circleStartAngle,
                circleEndAngle, false, circlePaint);
    }

    private int hintHeight = 0;

    private void drawHint(Canvas canvas) {
        String hints = TextUtils.isEmpty(hint) ? "正在努力评估中，请耐心等待..." : hint;
        float textWidth = hintPaint.measureText(hints);
        // 文本baseline在y轴方向的位置
        float baseLineY = Math.abs(hintPaint.ascent() + hintPaint.descent()) / 2;
        if (hintIsShow) {
            canvas.drawText(hints, width / 2 - textWidth / 2, (circlePadding + circleMargin) * 2 + circleBitmapHeight + baseLineY, hintPaint);
            hintHeight = (int) (hintPaint.density - hintPaint.ascent());
        } else {
            hintHeight = 0;
        }
    }
```
* 绘制下方文字
```
   private void drawBottom(Canvas cavas) {
        //剩下的距离，根据titles的标致均分
        remainingHeight = (int) (height - (circleMargin + circlePadding + lineMargin) * 2 - circleBitmapHeight - hintHeight - lineHeight);
        int y = (int) ((circleMargin + circlePadding + lineMargin) * 2 + circleBitmapHeight + hintHeight + lineHeight);
        int everyItem = remainingHeight / titles.size();
        //配置当前的一些常见属性

        //去左上角的点
        Point iconPoint = new Point(dp2px(mContext, 40), y + everyItem * 3 / 11);
        Point titlePoint = new Point(iconPoint.x+dp2px(mContext, 40), iconPoint.y);
        Point contentPoint = new Point(iconPoint.x+dp2px(mContext, 41), iconPoint.y + dp2px(mContext, 30));
        bottomBitmap(cavas, iconPoint, step);
        titleCenter(titles, titlePaint, cavas, titlePoint, Paint.Align.CENTER, step);
        contentCenter(contentSelect, contentUnSelect, contentPaint, cavas, contentPoint, Paint.Align.CENTER, step);
        ;
    }

    private void bottomBitmap(Canvas canvas, Point point, int step) {
        Bitmap bitmapUnSelect = BitmapFactory.decodeResource(getResources(), bottomUnSelectIcon);
        Bitmap bitmapSelect = BitmapFactory.decodeResource(getResources(), bottomSelectIcon);

        for (int i = 0; i < titles.size(); i++) {
            float yAxis = remainingHeight / titles.size();
            if (i <= step) {
                canvas.drawBitmap(bitmapSelect, null, new Rect(point.x,
                                point.y + (int) yAxis * i,
                                point.x + (int) bottomIconWidth,
                                point.y + (int) bottomIconHeight + (int) yAxis * i)
                        , bitmapPaint);
            } else {
                canvas.drawBitmap(bitmapUnSelect, null, new RectF(point.x,
                                point.y + (int) yAxis * i,
                                point.x + bottomIconWidth,
                                point.y + bottomIconHeight + (int) yAxis * i)
                        , bitmapPaint);
            }

        }
        for (int i = 1; i < titles.size(); i++) {
            if (i <= step) {
                connectLinePaint.setColor(chooseColor);
            } else {
                connectLinePaint.setColor(defaultColor);
            }
            float yAxis = remainingHeight / titles.size();
            canvas.drawLine(point.x + bottomIconWidth / 2, bottomIconHeight + point.y + yAxis * (i - 1)
                    , point.x + bottomIconWidth / 2, point.y + yAxis * i, connectLinePaint);
        }
        bitmapSelect.recycle();
        bitmapSelect = null;
        bitmapUnSelect.recycle();
        bitmapUnSelect = null;

    }

    //绘制标题
    private void titleCenter(List<String> strings, Paint paint, Canvas canvas, Point point, Paint.Align aligin, int step) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float ascent = fontMetrics.ascent;
        float descent = fontMetrics.descent;
        float stringHeight = descent - ascent;
        int length = strings.size();
        for (int i = 0; i < length; i++) {
            if (i <= step) {
                titlePaint.setColor(chooseColor);
            } else {
                titlePaint.setColor(defaultColor);
            }
            float yAxis = remainingHeight / length;
            canvas.drawText(strings.get(i) + "", point.x, point.y + yAxis * i + stringHeight, paint);
        }
    }

    //绘制内容
    private void contentCenter(List<String> select, List<String> unselect, Paint paint, Canvas canvas, Point point, Paint.Align aligin, int step) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float ascent = fontMetrics.ascent;
        float descent = fontMetrics.descent;
        float stringHeight = descent - ascent;
        for (int i = 0; i < titles.size(); i++) {
            float yAxis = remainingHeight / titles.size();
            if (i <= step) {
                contentPaint.setColor(chooseColor);
                canvas.drawText(select.get(i) + "", point.x, point.y + yAxis * i + stringHeight, paint);
            } else {
                contentPaint.setColor(defaultColor);
                canvas.drawText(unselect.get(i) + "", point.x, point.y + yAxis * i + stringHeight, paint);
            }
        }
    }
```
* 设置回调方法
```
    public interface onViewCompleteFinshListener {
        void complete();
    }

    public void setCompleteFinshListener(onViewCompleteFinshListener completeFinshListener) {
        this.completeFinshListener = completeFinshListener;
    }

public class MainActivity extends AppCompatActivity implements SmartLoadingView.onViewCompleteFinshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SmartLoadingView loadingView = findViewById(R.id.smartView);
        loadingView.setCompleteFinshListener(this);
    }

    @Override
    public void complete() {
        Toast.makeText(this, "进度已经加载完毕，该执行对应的跳转方法了", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, NextActivity.class));
        this.finish();
    }


}

```

  3.**在xml文件中调用**
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <combudou.smartloadingview.view.SmartLoadingView
        android:id="@+id/smartView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:contentSelect="@array/verify_content"
        app:contentUnSelect="@array/default_content"
        app:titles="@array/titles" />


</RelativeLayout>
```

##### 实现思路
功能很简单，都是一些基本的canvas方法调用，实现这个算是对基础知识的一个回顾吧。

