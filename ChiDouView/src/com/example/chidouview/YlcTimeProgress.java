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
 * �Զ�ʱ������Զ���ؼ�
 * @author yanlc
 *
 */
public class YlcTimeProgress extends View{
	/**
	 * ���Ӽ�ļ��
	 */
	private float mPadding = dip2px(getContext(), 10);
	/**
	 * �����Ⱥ�ִ������
	 */
	private long maxProgress = 20;  
	/**
	 * ��ǰ�Ľ���
	 */
	private int currentProgress = -1; 
	/**
	 * ��ǰ���ȵ���һ������
	 */
	private int nextProgress  = 0; 	
	/**
	 * ���
	 */
	private int viewW = 0;
	private int ViewH = 0;
	/**
	 * ����
	 */
	private Paint mPaint;
	private Bitmap mDouZzBitmap;
	private Bitmap mDouBzBitmap;
	/**
	 * װ���ӵ�list
	 */
	public ArrayList<Float> mCircleList = new ArrayList<Float>();
	/**
	 * ��ʼʱ�Զ���left�߾�����߾�ľ���
	 */
	public float mPaidngLeft = dip2px(getContext(), 10);
	/**
	 * ��һ�����Ӿ�����߾�ľ���
	 */
	public float mDouStartLoc = 0;
	/**
	 * �Զ��˵�ǰ��λ��
	 */
	private float mCurrentLoc = mPaidngLeft;  
	 /**
	  * �����Ƿ�ִ��
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
		 * ��ͣ״̬
		 */
		private boolean isPause = false;
		/**
		 * �Ƿ��Ѿ���ͣ�����һ�Ѿ���ͣ����ô�Ͳ���Ҫ�ٴ�����ֹͣ��һЩ�¼��ͼ�������
		 */
		private boolean isPaused = false;
		/**
		 * ��ǰ�Ķ����Ĳ���λ��
		 */
		private float fraction = 0.0f;
		/**
		 * ��ǰ�����Ĳ�������ʱ��
		 */
		private long mCurrentPlayTime = 0l;
				
		/**
		 * �Ƿ�����ͣ״̬
		 * @return
		 */
		public boolean isPause(){
			return isPause;
		}
		
		/**
		 * ֹͣ������ֻ�����ñ�־λ��ʣ��Ĺ��������״̬λ����onAnimationUpdate���в���
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
			 * �������ͣ��״̬������������ÿ��ˢ�¶�����ʱ���ˣ������õ�ǰʱ�䣬�ö���
			 * ��ʱ���ϴ�����ͣ״̬��ͬʱҪ����һ����ֹ��ʱ�������������֤�������ᶶ��
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
				//ÿ���������ŵ�ʱ�䣬���Ƕ��Ὣ����ʱ�����ص������Ա����²��ŵ�ʱ�����ʹ�����ʱ��,ͬʱҲΪ������������������
				timer.start();
			} else {
				//��ʱ���������ָ������Եģ���������Լ��ģ�Ҳ������������лָ�
				animation.setInterpolator(null);
				mCurrentLoc = (Float) animation.getAnimatedValue();	
				
				//���½��� ����home��֮���Կɵ���
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
				this.cancel(); //����ȡ����������ڴ����
			}

			@Override
			public void onFinish() {
				if(animator != null)
				  animator.setCurrentPlayTime(mCurrentPlayTime);
			}
		};
	}

	

}
