package com.example.yzy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义流式布局
 */
public class FlowLayout extends ViewGroup{

    private static final String TAG = "print";

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 返回当前布局的LayoutParams
     * @param attrs
     * @return 指定当前布局使用的布局参数为MarginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 测量布局的宽高
     *
     * 布局的宽 = 最宽的一行
     * 布局的高 = 每一行最高的子控件之和
     *
     * 在onMeasure方法内 得不到子控件的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        /**
         * 测量所有子控件的宽高
         *
         * 通过测量:
         * getMeasuredWidth()
         * getMeasuredHeight()
         * 获得宽高
         */
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);//获得宽度模式
        int wSize = MeasureSpec.getSize(widthMeasureSpec);//获得系统的推荐值
        int hMode = MeasureSpec.getMode(heightMeasureSpec);//获得高度模式
        int hSize = MeasureSpec.getSize(heightMeasureSpec);//获得高度的推荐值


        int width = 0;//布局的总宽度
        int height = 0;//布局的总高度
        int lineWidth = 0;//当前行的宽度
        int lineHeight = 0;//当前行的高度

        /**
         * 循环所有子控件
         */
        for(int i = 0; i < getChildCount(); i++){
            View chilview = getChildAt(i);
//            Log.d(TAG, "onMeasure: 子控件的宽高：" + chilview.getMeasuredWidth() + "  " + chilview.getMeasuredHeight());

            MarginLayoutParams layoutParams = (MarginLayoutParams) chilview.getLayoutParams();
            int cwidth = chilview.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;//当前控件所占的宽度
            int cheight = chilview.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;//当前控件所占的高度

            if(lineWidth + cwidth > wSize){//如果当前行的宽度 + 当前子控件的宽度 > 系统推荐的宽度 表示要换行
                width = Math.max(width, lineWidth);
                height += lineHeight;

                //重新计算新的一行
                lineHeight = cheight;
                lineWidth = cwidth;
            } else {
                //当前行能够摆放
                lineWidth += cwidth;
                lineHeight = Math.max(lineHeight, cheight);//取当前行的高度和当前子控件高度的一个最大值
            }

            //如果当前计算的子控件为最后一个子控件，那么需要把当前行的宽高，更新到布局宽高中
            if(i == getChildCount() - 1){
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        Log.d(TAG, "onMeasure: 布局的总宽高：" + width + "   " + height);
        setMeasuredDimension(
                wMode == MeasureSpec.EXACTLY?wSize:width,
                hMode == MeasureSpec.EXACTLY?hSize:height);//保存你的宽高

    }

    /**
     * 控制子控件位置摆放
     * @param changed
    * @param l
    * @param t
    * @param r
    * @param b
    */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int lineWidth = 0;//当前行的宽度
        int lineHeigth = 0;//当前行的最大高度
        int heigth = 0;//布局的总高度

        for(int i = 0; i < getChildCount(); i++){
            View chilview = getChildAt(i);

            MarginLayoutParams layoutParams = (MarginLayoutParams) chilview.getLayoutParams();
            int cwidth = chilview.getMeasuredWidth();
            int cheight = chilview.getMeasuredHeight();
            int cLeftMargin = layoutParams.leftMargin;
            int cRigthMargin = layoutParams.rightMargin;
            int cTopMargin = layoutParams.topMargin;
            int cBottomMargin = layoutParams.bottomMargin;

            Log.d(TAG, "onLayout: 子控件的宽高：" + cwidth + "  " + cheight);

            //换行判断
            if(lineWidth + cwidth + cLeftMargin + cRigthMargin > getWidth()){
                //表示换行
                lineWidth = 0;
                heigth += lineHeigth;
                lineHeigth = 0;
            }

            //控件的位置
            l = lineWidth + cLeftMargin;
            t = heigth + cTopMargin;
            r = lineWidth + cLeftMargin + cwidth;
            b = heigth + cTopMargin + cheight;

            lineWidth += cwidth + cLeftMargin + cRigthMargin;
            lineHeigth = Math.max(lineHeigth, cheight + cTopMargin + cBottomMargin);

            Log.d(TAG, "onLayout: 子控件的摆放位置：" + l + "  " + t + "  " + r + "  " + b);

            chilview.layout(l, t, r, b);
        }
    }
}
