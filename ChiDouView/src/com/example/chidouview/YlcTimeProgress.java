package com.example.chidouview;

import java.util.ArrayList;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/**
 * 
 * 吃豆时间进度自定义控件
 * @author yanlc
 *
 */
public class YlcTimeProgress extends View{
	/**
	 * 豆子间的间距
	 */
	private float mPadding = dip2px(getContext(), 10);
	/**
	 * 最大进度和执行秒数
	 */
	private long maxProgress = 20;  
	/**
	 * 当前的进度
	 */
	private int currentProgress = -1; 
	/**
	 * 当前进度的下一个进度
	 */
	private int nextProgress  = 0; 	
	/**
	 * 宽高
	 */
	private int viewW = 0;
	private int ViewH = 0;
	/**
	 * 画笔
	 */
	private Paint mPaint;
	private Bitmap mDouZzBitmap;
	private Bitmap mDouBzBitmap;
	/**
	 * 装豆子的list
	 */
	public ArrayList<Float> mCircleList = new ArrayList<Float>();
	/**
	 * 初始时吃豆人left边距离左边距的距离
	 */
	public float mPaidngLeft = dip2px(getContext(), 10);
	/**
	 * 第一个豆子距离左边距的距离
	 */
	public float mDouStartLoc = 0;
	/**
	 * 吃豆人当前的位置
	 */
	private float mCurrentLoc = mPaidngLeft;  
	 /**
	  * 动画是否执行
	  */
	public boolean mAnimStarting = false;
	public MyAnimatorUpdateListener mAnimatorUpdateListener;
	public ValueAnimator animator;
		
	public YlcTimeProgress(Context context) {
		super(context);
		init();
	}

	public YlcTimeProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public YlcTimeProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public float getMaxProgress(){
		return maxProgress;
	}
	
	private void init(){		
		mPaint = new Paint();
		mPaint.setColor(Color.parseColor("#FDDDD4"));	
		mPaint.setAntiAlias(true);
		mDouZzBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chidou);
		mDouBzBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.chidou2);		
		mDouStartLoc = mDouZzBitmap.getWidth()+mPaidngLeft*2;
		mAnimatorUpdateListener = new MyAnimatorUpdateListener();
	}
	
	@SuppressLint("NewApi") @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(int i=0;i<maxProgress;i++){
			if(mCurrentLoc+mDouBzBitmap.getWidth()<i*mPadding+mDouStartLoc){
				mPaint.setColor(Color.parseColor("#F8CBBC"));
				canvas.drawCircle(i*mPadding+mDouStartLoc, ViewH/2, dip2px(getContext(), 3), mPaint);
				mPaint.setColor(Color.parseColor("#FDDDD4"));
				canvas.drawCircle(i*mPadding+mDouStartLoc, ViewH/2, dip2px(getContext(), 2), mPaint);
		      } 
		}
		
		if(mCurrentLoc+mDouBzBitmap.getWidth()>nextProgress*mPadding+mDouStartLoc-mPadding/2){
		   canvas.drawBitmap(mDouZzBitmap, mCurrentLoc, ViewH/2-mDouBzBitmap.getHeight()/2, mPaint);
		} else {
		   canvas.drawBitmap(mDouBzBitmap, mCurrentLoc, ViewH/2-mDouBzBitmap.getHeight()/2, mPaint);
		}
		
		if(!mAnimStarting){
			animator = ValueAnimator.ofFloat(mPaidngLeft,viewW-mPaidngLeft-mDouBzBitmap.getWidth());
			animator.setDuration(maxProgress*1000);
			animator.addUpdateListener(mAnimatorUpdateListener);
			mAnimStarting = true;
	     }
	}
		
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        viewW = widthSize;
        ViewH = heightSize;
        
		mPadding = (viewW-mDouStartLoc)/maxProgress;
		mCircleList.clear();
		for(int i=0;i<maxProgress;i++){
			mCircleList.add(i*mPadding+mDouStartLoc);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
		
	public static int dip2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public void start(){
		if(animator != null)
		   animator.start();
	}
	
	public void reStart(){
		mCurrentLoc = mPaidngLeft;
		if(animator != null){
			resume();
		   animator.start();
		}
	}
	
	public void pause(){
		if(mAnimatorUpdateListener != null)
			mAnimatorUpdateListener.pause();
	}
	
	public void resume(){
		if(mAnimatorUpdateListener != null)
			mAnimatorUpdateListener.play();
	}
	
	
	@SuppressLint("NewApi")
   class MyAnimatorUpdateListener implements AnimatorUpdateListener{
		/**
		 * 暂停状态
		 */
		private boolean isPause = false;
		/**
		 * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
		 */
		private boolean isPaused = false;
		/**
		 * 当前的动画的播放位置
		 */
		private float fraction = 0.0f;
		/**
		 * 当前动画的播放运行时间
		 */
		private long mCurrentPlayTime = 0l;
				
		/**
		 * 是否是暂停状态
		 * @return
		 */
		public boolean isPause(){
			return isPause;
		}
		
		/**
		 * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
		 */
		public void pause(){
			isPause = true;
		}
		public void play(){
			timer.cancel();
			isPause = false;
			isPaused = false;
		}
			
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			/**
			 * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
			 * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
			 */
			if(isPause){
				if(!isPaused){
					mCurrentPlayTime = animation.getCurrentPlayTime();
					fraction = animation.getAnimatedFraction();
					animation.setInterpolator(new TimeInterpolator() {
						@Override
						public float getInterpolation(float input) {
							return fraction;
						}
					});
					isPaused =  true;
				}
				//每隔动画播放的时间，我们都会将播放时间往回调整，以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
				timer.start();
			} else {
				//将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
				animation.setInterpolator(null);
				mCurrentLoc = (Float) animation.getAnimatedValue();	
				
				//更新进度 当按home键之后仍可调用
				int tempIndex = -1;
				for(int i=0;i<maxProgress;i++){
					if(mCurrentLoc+mDouBzBitmap.getWidth()<i*mPadding+mDouStartLoc){
				      } else {
				    	  tempIndex = i;
				      }
				}
				if(currentProgress != tempIndex) {
				    currentProgress = tempIndex;
				    nextProgress = currentProgress +1;
				    Log.e("debug", "currentProgress = "+currentProgress);
				}		
				invalidate();
			}
		}	
		
		public CountDownTimer timer = new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()){

			@Override
			public void onTick(long millisUntilFinished) {
				this.cancel(); //调用取消，否则会内存溢出
			}

			@Override
			public void onFinish() {
				if(animator != null)
				  animator.setCurrentPlayTime(mCurrentPlayTime);
			}
		};
	}

	

}
