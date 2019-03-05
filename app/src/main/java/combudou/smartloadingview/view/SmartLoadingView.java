package combudou.smartloadingview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import combudou.smartloadingview.R;

/**
 * package:combudou.smartloadingview.view
 * author:布兜小爱
 * e-main:budou1994@qq.com
 * date:2019年03月03日
 * desc:创建一个自定义的加载页面，加载对应的数据
 */
@SuppressWarnings("ALL")
public class SmartLoadingView extends View {

    private static final String TAG = SmartLoadingView.class.getSimpleName();
    //    上下文
    private Context mContext;
    //    是否开启抗锯齿  会对手机性能造成一定的影响
    private boolean antiAlias;
    //    view的背景
    private int allBack;
    //    进度条中间展示view
    private int circleBitmap;
    //    进度条中间view 展示宽度
    private float circleBitmapWidth;
    //    进度条中间view 展示高度
    private float circleBitmapHeight;
    //    是否展示改view
    private boolean isShowIcon;
    //    内部padding
    private float circlePadding;
    //    进度条的宽度
    private float circleWidth;
    //    进度条外部距离
    private float circleMargin;
    //    进度条背景颜色
    private int circleBgColor;
    //    进度展示颜色
    private int circleColor;
    //    多长时长循环一次
    private int circleShowTime;
    //    进度起始角度
    private float circleStartAngle;
    //    进度终止角度
    private float circleEndAngle;
    //    提示字
    private String hint;
    //    是否展示提示字
    private boolean hintIsShow;
    //    提示字大小
    private float hintSize;
    //    提示字颜色
    private int hintColor;
    //    分割线高度
    private float lineHeight;
    //    分割线margin
    private float lineMargin;
    //    分割线颜色
    private int lineColor;
    //    未选中的icon图标
    private int bottomUnSelectIcon;
    //    选中的icon图标
    private int bottomSelectIcon;
    //    icon宽度
    private float bottomIconWidth;
    //    icon高度
    private float bottomIconHeight;
    //    bottom默认展示颜色
    private int defaultColor;
    //    bottom选中展示颜色
    private int chooseColor;
    //    bottom标题的颜色
    private int titleColor;
    //    连接线的宽度
    private float connectlineWidth;
    //    内容字体大小
    private float contentSize;
    //    标题字体大小
    private float titleSize;
    //    提示字绘制
    private TextPaint hintPaint;
    //    标题绘制
    private TextPaint titlePaint;
    //    内容绘制
    private TextPaint contentPaint;
    //    进度背景绘制
    private Paint circleBgPaint;
    //    进度绘制
    private Paint circlePaint;
    //    bitmap绘制
    private Paint bitmapPaint;
    //    分割线
    private Paint cutLinePaint;
    //     连接线
    private Paint connectLinePaint;
    List<String> titles = new ArrayList<>();//标题集合
    List<String> contentUnSelect = new ArrayList<>();//未选中字体集合
    List<String> contentSelect = new ArrayList<>();//选中字体集合
    int width, height;
    private int radius;//圆半径
    private int remainingHeight;
    private Timer timer;
    private float middleANgle;

    private Timer checkTImer;
    private int step = -1;
    private boolean isComplete = false;
    private onViewCompleteFinshListener completeFinshListener;

    public SmartLoadingView(Context context) {
        this(context, null);
    }

    public SmartLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    //初始化view 的一些参数
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        initAttrs(attrs);
        initPaint();
        middleANgle = 360 / circleShowTime;
        timer = new Timer();
        checkTImer = new Timer();
        //进度条效果展示
        initTimer();
    }

    private void initTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 150);

        //
        checkTImer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 1500);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    circleStartAngle += middleANgle;
                    circleEndAngle += middleANgle;
                    if (circleEndAngle >= 360 || circleStartAngle >= 360) {
                        circleEndAngle = circleEndAngle % 360;
                        circleStartAngle = circleStartAngle % 360;
                    }
                    break;
                case 1:
                    step++;
                    Log.d("ss,", step + "***");
                    if (step >= titles.size()) {
                        step = titles.size();
                        checkTImer.cancel();
                        completeFinshListener.complete();
                    }
                    break;
            }
            invalidate();
        }
    };


    public interface onViewCompleteFinshListener {
        void complete();
    }

    public void setCompleteFinshListener(onViewCompleteFinshListener completeFinshListener) {
        this.completeFinshListener = completeFinshListener;
    }

    /**
     * 初始化一些自定义属性
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.SmartLoadingView);
        antiAlias = array.getBoolean(R.styleable.SmartLoadingView_antiAlias, true);//默认打开抗锯齿
        //默认展示白色背景
        allBack = array.getColor(R.styleable.SmartLoadingView_allBack, Color.WHITE);
        //默认展示的图片
        circleBitmap = array.getInt(R.styleable.SmartLoadingView_circleBitmap, R.mipmap.loading);
        //默认宽度48dp
        circleBitmapWidth = array.getDimension(R.styleable.SmartLoadingView_circleBitmapWidth, dp2px(mContext, 48));
        //默认宽度48dp
        circleBitmapHeight = array.getDimension(R.styleable.SmartLoadingView_circleBitmapHeight, dp2px(mContext, 48));
        //默认展示view
        isShowIcon = array.getBoolean(R.styleable.SmartLoadingView_isShowIcon, true);
        //默认内部5dp
        circlePadding = array.getDimension(R.styleable.SmartLoadingView_circlePadding, dp2px(mContext, 25));
        //默认进度条2dp宽
        circleWidth = array.getDimension(R.styleable.SmartLoadingView_circleWidth, dp2px(mContext, 3));
        //进度条对外距离5dp
        circleMargin = array.getDimension(R.styleable.SmartLoadingView_circleMargin, dp2px(mContext, 20));
        //进度条展示默认背景
        circleBgColor = array.getColor(R.styleable.SmartLoadingView_circleBgColor, getResources().getColor(R.color.defaultColor));
        //进度条展示view
        circleColor = array.getColor(R.styleable.SmartLoadingView_circleColor, getResources().getColor(R.color.selectColor));
        //6秒钟走完一个圆圈
        circleShowTime = array.getInt(R.styleable.SmartLoadingView_circleShowTime, 6);
        //进度圆弧默认开始角度
        circleStartAngle = array.getFloat(R.styleable.SmartLoadingView_circleStartAngle, 0f);
        //进度圆弧默认关闭角度
        circleEndAngle = array.getFloat(R.styleable.SmartLoadingView_circleEndAngle, 90f);
        //默认提示字
        hint = array.getString(R.styleable.SmartLoadingView_hint);
        //提示字颜色
        hintColor = array.getColor(R.styleable.SmartLoadingView_hintColor, getResources().getColor(R.color.defaultColor));
        //默认值16sp
        hintSize = array.getDimension(R.styleable.SmartLoadingView_hintSize, sp2px(mContext, 16));
        //默认展示提示字
        hintIsShow = array.getBoolean(R.styleable.SmartLoadingView_hintIsShow, true);
        //默认值30dp
        lineHeight = array.getDimension(R.styleable.SmartLoadingView_lineHeight, dp2px(mContext, 15));
        //默认值30dp
        lineMargin = array.getDimension(R.styleable.SmartLoadingView_lineMargin, dp2px(mContext, 5));
        lineColor = array.getColor(R.styleable.SmartLoadingView_lineColor, Color.parseColor("#F5F5F5"));
        //未选中展示icon
        bottomUnSelectIcon = array.getInt(R.styleable.SmartLoadingView_bottomUnSelectIcon, R.mipmap.un_select);
        //选中展示icon
        bottomSelectIcon = array.getInt(R.styleable.SmartLoadingView_bottomSelectIcon, R.mipmap.select);
        bottomIconWidth = array.getDimension(R.styleable.SmartLoadingView_bottomIconWidth, dp2px(mContext, 26));
        bottomIconHeight = array.getDimension(R.styleable.SmartLoadingView_bottomIconHeight, dp2px(mContext, 26));
        defaultColor = array.getColor(R.styleable.SmartLoadingView_defaultColor, getResources().getColor(R.color.defaultColor));
        chooseColor = array.getColor(R.styleable.SmartLoadingView_chooseColor, getResources().getColor(R.color.selectColor));
        titleColor = array.getColor(R.styleable.SmartLoadingView_titleColor, getResources().getColor(R.color.defaultColor));
        connectlineWidth = array.getDimension(R.styleable.SmartLoadingView_connectLineWidth, dp2px(mContext, 2));
        contentSize = array.getDimension(R.styleable.SmartLoadingView_contentSize, sp2px(mContext, 14));
        titleSize = array.getDimension(R.styleable.SmartLoadingView_titleSize, sp2px(mContext, 18));


        int contentUnSelectId = array.getResourceId(R.styleable.SmartLoadingView_contentUnSelect, 0);
        int contentSelectId = array.getResourceId(R.styleable.SmartLoadingView_contentSelect, 0);

        int titlesId = array.getResourceId(R.styleable.SmartLoadingView_titles, 0);
        try {
            if (titlesId != 0) {
                //标题不可为空，为空就直接抛出异常
                String[] titles = getResources().getStringArray(titlesId);
                if (titles.length < 2) {
                    throw new Exception("title字符串至少需要两个参数");
                } else {
                    this.titles = new ArrayList<>();
                    for (int i = 0; i < titles.length; i++) {
                        this.titles.add(titles[i]);
                    }
                }
            } else {
                throw new Exception("title字符串必须不为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //未选中和选中的内容均可以为空
        if (contentUnSelectId != 0) {
            String[] tempContentUnSelect = getResources().getStringArray(contentUnSelectId);
            for (int i = 0; i < titles.size(); i++) {
                if (i <= tempContentUnSelect.length) {
                    contentUnSelect.add(tempContentUnSelect[i]);
                } else {
                    contentUnSelect.add("");
                }
            }
        } else {
            for (int i = 0; i < titles.size(); i++) {
                contentUnSelect.add("");
            }
        }

        if (contentSelectId != 0) {
            String[] tempContentSelect = getResources().getStringArray(contentSelectId);
            for (int i = 0; i < titles.size(); i++) {
                if (i <= tempContentSelect.length) {
                    contentSelect.add(tempContentSelect[i]);
                } else {
                    contentSelect.add("");
                }
            }
        } else {
            for (int i = 0; i < titles.size(); i++) {
                contentSelect.add("");
            }
        }
        array.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //提示字
        hintPaint = new TextPaint();
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        hintPaint.setAntiAlias(antiAlias);
        hintPaint.setColor(hintColor);
        hintPaint.setTextSize(hintSize);
        //分割线
        cutLinePaint = new Paint();
        cutLinePaint.setAntiAlias(antiAlias);
        cutLinePaint.setColor(lineColor);
        cutLinePaint.setStyle(Paint.Style.FILL);
        //图片
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(antiAlias);
        //进度条背景
        circleBgPaint = new Paint();
        circleBgPaint.setAntiAlias(antiAlias);
        circleBgPaint.setStyle(Paint.Style.STROKE);
        circleBgPaint.setColor(circleBgColor);
        circleBgPaint.setStrokeWidth(circleWidth);
        //进度条背景
        circlePaint = new Paint();
        circlePaint.setAntiAlias(antiAlias);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleWidth);
        //连接线
        connectLinePaint = new Paint();
        connectLinePaint.setStrokeWidth(connectlineWidth);
        connectLinePaint.setColor(Color.parseColor("#e8e8e8"));
        //标题
        titlePaint = new TextPaint();
        titlePaint.setAntiAlias(antiAlias);
        titlePaint.setTextSize(titleSize);
        titlePaint.setColor(titleColor);
        titlePaint.setFakeBoldText(true);
        //内容
        contentPaint = new TextPaint();
        contentPaint.setAntiAlias(antiAlias);
        contentPaint.setTextSize(contentSize);
        contentPaint.setColor(defaultColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        drawProgress(canvas);
        drawHint(canvas);
        drawRect(canvas);
        drawBottom(canvas);

    }

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

    private void drawRect(Canvas canvas) {
        canvas.drawRect(0, (circleMargin + circlePadding) * 2 + circleBitmapHeight + hintHeight + lineMargin
                , width, (circleMargin + circlePadding) * 2 + circleBitmapHeight + hintHeight + lineMargin + lineHeight, cutLinePaint);
    }

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


    /**
     * 获取屏幕Metrics参数
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源

    }

}
