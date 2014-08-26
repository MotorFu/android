package com.motorfu.special.ResideMenu;

import android.R.raw;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.motorfu.special.kenburnsview.KenBurnsView;
import com.motorfu.special.util.T;
import com.motorfu.special.util.UgcAnimations;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.special.ResideMenu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: special Date: 13-12-10 Time: 下午10:44 Mail: specialcyci@gmail.com
 */
public class ResideMenu extends FrameLayout {

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZANTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;

	private ImageView imageViewShadow;
	private KenBurnsView imageViewBackground;
	private TableLayout layoutLeftMenu;
	private TableLayout layoutRightMenu;

	private RelativeLayout rl_left;
	private RelativeLayout rl_right;
	private RelativeLayout rl_temp;
	// private StretchScrollView scrollViewLeftMenu;
	// private StretchScrollView scrollViewRightMenu;
	// private StretchScrollView scrollViewMenu;
	/** the activity that view attach to */
	private Activity activity;
	/** the decorview of the activity */
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** the flag of menu open status */
	private boolean isOpened;
	private GestureDetector gestureDetector;
	private float shadowAdjustScaleX;
	private float shadowAdjustScaleY;
	/** the view which don't want to intercept touch event */
	private List<View> ignoredViews;
	private List<ResideMenuItem> leftMenuItems;
	private List<ResideMenuItem> rightMenuItems;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private OnMenuListener menuListener;
	private float lastRawX;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;
	private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
	// valid scale factor is between 0.0f and 1.0f.
	private float mScaleValue = 0.3f;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	// 弹出菜单栏部分
	private View mUgcLeftView;
	private View mUgcRightView;
	private RelativeLayout mUgcLayoutL;

	private ImageView iv_head;
	private TextView tv_username;
	private TextView tv_other;

	private ImageView mUgcL;
	private ImageView mUgcBgL;
	private ImageView mUgcVoiceL;
	private ImageView mUgcPhotoL;
	private ImageView mUgcRecordL;
	private ImageView mUgcLbsL;

	private RelativeLayout mUgcLayoutR;

	private ImageView mUgcR;
	private ImageView mUgcBgR;
	private ImageView mUgcVoiceR;
	private ImageView mUgcPhotoR;
	private ImageView mUgcRecordR;
	private ImageView mUgcLbsR;

	private String mHead="";
	private String mUserName="";
	private String mSignName="";
	private DisplayImageOptions mOptions=null;

	public ResideMenu(Context context) {
		super(context);
		initViews(context);
	}

	public ResideMenu(Context context, String head, String userName, String signName, DisplayImageOptions options) {
		super(context);
		this.mHead = head;
		this.mUserName = userName;
		this.mSignName = signName;
		this.mOptions = options;
		initViews(context);
	}

	private Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			imageViewBackground.pause();
		}
	};

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu, this);
		// scrollViewLeftMenu = (StretchScrollView) findViewById(R.id.sv_left_menu);
		// scrollViewRightMenu = (StretchScrollView) findViewById(R.id.sv_right_menu);
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		imageViewShadow = (ImageView) findViewById(R.id.iv_shadow);
		layoutLeftMenu = (TableLayout) findViewById(R.id.layout_left_menu);
		layoutRightMenu = (TableLayout) findViewById(R.id.layout_right_menu);
		imageViewBackground = (KenBurnsView) findViewById(R.id.kv_background);
		mHandler.postDelayed(runnable, 1000);// 延迟1秒再停止播放，，，防止界面卡顿现象

		initLeftMenu();
		initRightMenu();

	}

	private boolean mUgcLeftIsShowing = false;
	private boolean mUgcRightIsShowing = false;

	private void initLeftMenu() {
		// 头像布局
		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_other = (TextView) findViewById(R.id.tv_other);

		ImageLoader.getInstance().displayImage(mHead, iv_head, mOptions);
		tv_username.setText(mUserName);
		tv_other.setText(mSignName);

		mUgcLeftView = (View) findViewById(R.id.desktop_ugc_left);
		mUgcLayoutL = (RelativeLayout) mUgcLeftView.findViewById(R.id.ugc_layout);
		mUgcL = (ImageView) mUgcLeftView.findViewById(R.id.ugc);
		mUgcBgL = (ImageView) mUgcLeftView.findViewById(R.id.ugc_bg);
		mUgcVoiceL = (ImageView) mUgcLeftView.findViewById(R.id.ugc_voice);
		mUgcPhotoL = (ImageView) mUgcLeftView.findViewById(R.id.ugc_photo);
		mUgcRecordL = (ImageView) mUgcLeftView.findViewById(R.id.ugc_record);
		mUgcLbsL = (ImageView) mUgcLeftView.findViewById(R.id.ugc_lbs);
//		setListener(DIRECTION_LEFT);
		// Path监听
		mUgcLeftView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// Toast.makeText(mContext, "=====" + mUgcIsShowing, 1000).show();
				// 判断是否已经显示,显示则关闭并隐藏
				if (mUgcLeftIsShowing) {
					mUgcLeftIsShowing = false;
					UgcAnimations.startCloseAnimation(mUgcLayoutL, mUgcBgL, mUgcL, 500);
					return true;
				}
				return false;
			}
		});
		// Path监听
		mUgcL.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(mContext, "=====" + mUgcIsShowing, 1000).show();
				// 判断是否显示,已经显示则隐藏,否则则显示
				mUgcLeftIsShowing = !mUgcLeftIsShowing;
				if (mUgcLeftIsShowing) {
					UgcAnimations.startOpenAnimation(mUgcLayoutL, mUgcBgL, mUgcL, 500);
				} else {
					UgcAnimations.startCloseAnimation(mUgcLayoutL, mUgcBgL, mUgcL, 500);
				}
			}
		});
	}

	private void initRightMenu() {
		mUgcRightView = (View) findViewById(R.id.desktop_ugc_right);
		mUgcLayoutR = (RelativeLayout) mUgcRightView.findViewById(R.id.ugc_layout_r);
		mUgcR = (ImageView) mUgcRightView.findViewById(R.id.ugc_r);
		mUgcBgR = (ImageView) mUgcRightView.findViewById(R.id.ugc_bg_r);
		mUgcVoiceR = (ImageView) mUgcRightView.findViewById(R.id.ugc_voice_r);
		mUgcPhotoR = (ImageView) mUgcRightView.findViewById(R.id.ugc_photo_r);
		mUgcRecordR = (ImageView) mUgcRightView.findViewById(R.id.ugc_record_r);
		mUgcLbsR = (ImageView) mUgcRightView.findViewById(R.id.ugc_lbs_r);
		setListenerRight(DIRECTION_RIGHT);
		// Path监听
		mUgcRightView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// Toast.makeText(mContext, "=====" + mUgcIsShowing, 1000).show();
				// 判断是否已经显示,显示则关闭并隐藏
				if (mUgcRightIsShowing) {
					mUgcRightIsShowing = false;
					UgcAnimations.startCloseAnimationRight(mUgcLayoutR, mUgcBgR, mUgcR, 500);
					return true;
				}
				return false;
			}
		});
		// Path监听
		mUgcR.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(mContext, "=====" + mUgcIsShowing, 1000).show();
				// 判断是否显示,已经显示则隐藏,否则则显示
				mUgcRightIsShowing = !mUgcRightIsShowing;
				if (mUgcRightIsShowing) {
					UgcAnimations.startOpenAnimationRight(mUgcLayoutR, mUgcBgR, mUgcR, 500);
				} else {
					UgcAnimations.startCloseAnimationRight(mUgcLayoutR, mUgcBgR, mUgcR, 500);
				}
			}
		});
	}
	
	public void leftMenuClickListener(OnClickListener listener1,OnClickListener listener2,OnClickListener listener3,OnClickListener listener4){
		mUgcVoiceL.setOnClickListener(listener1);
		mUgcPhotoL.setOnClickListener(listener2);
		mUgcRecordL.setOnClickListener(listener3);
		mUgcLbsL.setOnClickListener(listener4);
	}
	
	/**
	 * UI事件监听
	 */
	private void setListener(final int direction) {
		// 头布局监听

		// Path 语音按钮监听
		mUgcVoiceL.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						T.showShort(getContext(), "语音");
						// mContext.startActivity(new Intent(mContext,
						// VoiceActivity.class));
						closeUgc(direction);
					}
				});
				mUgcVoiceL.startAnimation(anim);
			}
		});
		// Path 拍照按钮监听
		mUgcPhotoL.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						T.showShort(getContext(), "图片");
						closeUgc(direction);
					}
				});
				mUgcPhotoL.startAnimation(anim);
			}
		});
		// Path 记录按钮监听
		mUgcRecordL.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						// mContext.startActivity(new Intent(mContext,
						// WriteRecordActivity.class));
						closeUgc(direction);
					}
				});
				mUgcRecordL.startAnimation(anim);
			}
		});
		// Path 签到按钮监听
		mUgcLbsL.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						// mContext.startActivity(new Intent(mContext,
						// CheckInActivity.class));
						closeUgc(direction);
					}
				});
				mUgcLbsL.startAnimation(anim);
			}
		});

	}

	/**
	 * UI事件监听
	 */
	private void setListenerRight(final int direction) {
		// 头布局监听

		// Path 语音按钮监听
		mUgcVoiceR.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						// mContext.startActivity(new Intent(mContext,
						// VoiceActivity.class));
						closeUgc(direction);
					}
				});
				mUgcVoiceR.startAnimation(anim);
			}
		});
		// Path 拍照按钮监听
		mUgcPhotoR.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						closeUgc(direction);
					}
				});
				mUgcPhotoR.startAnimation(anim);
			}
		});
		// Path 记录按钮监听
		mUgcRecordR.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						// mContext.startActivity(new Intent(mContext,
						// WriteRecordActivity.class));
						closeUgc(direction);
					}
				});
				mUgcRecordR.startAnimation(anim);
			}
		});
		// Path 签到按钮监听
		mUgcLbsR.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						// mContext.startActivity(new Intent(mContext,
						// CheckInActivity.class));
						closeUgc(DIRECTION_RIGHT);
					}
				});
				mUgcLbsR.startAnimation(anim);
			}
		});

	}

	/**
	 * 关闭Path菜单
	 */
	public void closeUgc(int direction) {
		if (direction == DIRECTION_LEFT && mUgcLeftIsShowing) {
			mUgcLeftIsShowing = false;
			UgcAnimations.startCloseAnimation(mUgcLayoutL, mUgcBgL, mUgcL, 500);
		}
		if (direction == DIRECTION_RIGHT && mUgcRightIsShowing) {
			mUgcRightIsShowing = false;
			UgcAnimations.startCloseAnimationRight(mUgcLayoutR, mUgcBgR, mUgcR, 500);
		}
	}

	/**
	 * use the method to set up the activity which residemenu need to show;
	 * 
	 * @param activity
	 */
	public void attachToActivity(Activity activity) {
		initValue(activity);
		setShadowAdjustScaleXByOrientation();
		viewDecor.addView(this, 0);
		setViewPadding();

	}

	private void initValue(Activity activity) {
		this.activity = activity;
		leftMenuItems = new ArrayList<ResideMenuItem>();
		rightMenuItems = new ArrayList<ResideMenuItem>();
		ignoredViews = new ArrayList<View>();
		viewDecor = (ViewGroup) activity.getWindow().getDecorView();
		viewActivity = new TouchDisableView(this.activity);

		View mContent = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);
		viewActivity.setContent(mContent);
		addView(viewActivity);
	}

	private void setShadowAdjustScaleXByOrientation() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			shadowAdjustScaleX = 0.034f;
			shadowAdjustScaleY = 0.12f;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			shadowAdjustScaleX = 0.06f;
			shadowAdjustScaleY = 0.07f;
		}
	}

	public void bgPause() {
		imageViewBackground.pause();
	}

	public void bgResume() {
		imageViewBackground.resume();
	}

	/**
	 * 图片自动播放时间播放
	 * 
	 * @param duration
	 */
	public void setBgDuration(long duration) {
		imageViewBackground.setDuration(duration);
	}

	/**
	 * set the menu background picture;
	 * 
	 * @param imageResrouce
	 */
	public void setBackground(int imageResrouce) {
		imageViewBackground.setImageResource(imageResrouce);
	}

	public void setBackgroundByUrl(String url, DisplayImageOptions options) {
		imageLoader.displayImage(url, imageViewBackground, options);
	}

	/**
	 * the visiblity of shadow under the activity view;
	 * 
	 * @param isVisible
	 */
	public void setShadowVisible(boolean isVisible) {
		if (isVisible)
			imageViewShadow.setImageResource(R.drawable.shadow);
		else
			imageViewShadow.setImageBitmap(null);
	}

	/**
	 * add a single items to left menu;
	 * 
	 * @param menuItem
	 */
	@Deprecated
	public void addMenuItem(ResideMenuItem menuItem) {
		this.leftMenuItems.add(menuItem);
		layoutLeftMenu.addView(menuItem);
	}

	/**
	 * add a single items;
	 * 
	 * @param menuItem
	 * @param direction
	 */
	public void addMenuItem(ResideMenuItem menuItem, int direction) {
		if (direction == DIRECTION_LEFT) {
			this.leftMenuItems.add(menuItem);
			layoutLeftMenu.addView(menuItem);
		} else {
			this.rightMenuItems.add(menuItem);
			layoutRightMenu.addView(menuItem);
		}

	}

	/**
	 * set the menu items by array list to left menu;
	 * 
	 * @param menuItems
	 */
	@Deprecated
	public void setMenuItems(List<ResideMenuItem> menuItems) {
		this.leftMenuItems = menuItems;
		rebuildMenu();
	}

	/**
	 * set the menu items by array list;
	 * 
	 * @param menuItems
	 * @param direction
	 */
	public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
		if (direction == DIRECTION_LEFT)
			this.leftMenuItems = menuItems;
		else
			this.rightMenuItems = menuItems;
		rebuildMenu();
	}

	private void rebuildMenu() {
		layoutLeftMenu.removeAllViews();
		layoutRightMenu.removeAllViews();
		for (int i = 0; i < leftMenuItems.size(); i++) {
			layoutLeftMenu.addView(rightMenuItems.get(i), i);
		}
		for (int i = 0; i < rightMenuItems.size(); i++) {
			layoutRightMenu.addView(rightMenuItems.get(i), i);
		}
	}

	/**
	 * get the left menu items;
	 * 
	 * @return
	 */
	@Deprecated
	public List<ResideMenuItem> getMenuItems() {
		return leftMenuItems;
	}

	/**
	 * get the menu items;
	 * 
	 * @return
	 */
	public List<ResideMenuItem> getMenuItems(int direction) {
		if (direction == DIRECTION_LEFT)
			return leftMenuItems;
		else
			return rightMenuItems;
	}

	/**
	 * if you need to do something on the action of closing or opening menu, set the listener here.
	 * 
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * we need the call the method before the menu show, because the padding of activity can't get at the moment of onCreateView();
	 */
	private void setViewPadding() {
		this.setPadding(viewActivity.getPaddingLeft(), viewActivity.getPaddingTop(), viewActivity.getPaddingRight(), viewActivity.getPaddingBottom());
	}

	/**
	 * show the reside menu;
	 */
	public void openMenu(int direction) {

		setScaleDirection(direction);
		isOpened = true;
		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity, mScaleValue, mScaleValue);
		AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow, mScaleValue + shadowAdjustScaleX, mScaleValue + shadowAdjustScaleY);
		AnimatorSet alpha_menu = buildMenuAnimation(rl_temp, 1.0f);
		scaleDown_shadow.addListener(animationListener);
		scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.start();
	}

	/**
	 * close the reslide menu;
	 */
	public void closeMenu() {

		isOpened = false;
		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity, 1.0f, 1.0f);
		AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow, 1.0f, 1.0f);
		AnimatorSet alpha_menu = buildMenuAnimation(rl_temp, 0.0f);
		scaleUp_activity.addListener(animationListener);
		scaleUp_activity.playTogether(scaleUp_shadow);
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.start();
	}

	@Deprecated
	public void setDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	public void setSwipeDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	private boolean isInDisableDirection(int direction) {
		return disabledSwipeDirection.contains(direction);
	}

	private void setScaleDirection(int direction) {

		int screenWidth = getScreenWidth();
		float pivotX;
		float pivotY = getScreenHeight() * 0.62f;

		if (direction == DIRECTION_LEFT) {
			rl_temp = rl_left;
			pivotX = screenWidth * 2.0f;

		} else {
			rl_temp = rl_right;
			pivotX = screenWidth * -1.0f;

		}

		ViewHelper.setPivotX(viewActivity, pivotX);
		ViewHelper.setPivotY(viewActivity, pivotY);
		ViewHelper.setPivotX(imageViewShadow, pivotX);
		ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * return the flag of menu status;
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				rl_temp.setVisibility(VISIBLE);
				if (menuListener != null) {
					imageViewBackground.resume();
					menuListener.openMenu();

				}

			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);
				rl_temp.setVisibility(GONE);
				if (menuListener != null) {
					imageViewBackground.pause();
					menuListener.closeMenu();

				}

			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

	/**
	 * a helper method to build scale down animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target, float targetScaleX, float targetScaleY) {

		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX), ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(250);
		return scaleDown;
	}

	/**
	 * a helper method to build scale up animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX, float targetScaleY) {

		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX), ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleUp.setDuration(250);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha) {

		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha", alpha));

		alphaAnimation.setDuration(250);
		return alphaAnimation;
	}

	/**
	 * if there ware some view you don't want reside menu to intercept their touch event,you can use the method to set.
	 * 
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * remove the view from ignored view list;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * clear the ignored view list;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * if the motion evnent was relative to the view which in ignored view list,return true;
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	private float getTargetScale(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX : scaleFloatX;

		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.3f ? 0.3f : targetScale;
		return targetScale;
	}

	private float lastActionDownX, lastActionDownY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
		if (currentActivityScaleX == 1.0f)
			setScaleDirectionByRawX(ev.getRawX());

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastActionDownX = ev.getX();
			lastActionDownY = ev.getY();
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			if (isInIgnoredView || isInDisableDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN && pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			int xOffset = (int) (ev.getX() - lastActionDownX);
			int yOffset = (int) (ev.getY() - lastActionDownY);

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {
					pressedState = PRESSED_MOVE_VERTICAL;
					break;
				}
				if (xOffset < -50 || xOffset > 50) {
					pressedState = PRESSED_MOVE_HORIZANTAL;
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZANTAL) {
				if (currentActivityScaleX < 0.95)
					rl_temp.setVisibility(VISIBLE);

				float targetScale = getTargetScale(ev.getRawX());
				ViewHelper.setScaleX(viewActivity, targetScale);
				ViewHelper.setScaleY(viewActivity, targetScale);
				ViewHelper.setScaleX(imageViewShadow, targetScale + shadowAdjustScaleX);
				ViewHelper.setScaleY(imageViewShadow, targetScale + shadowAdjustScaleY);
				ViewHelper.setAlpha(rl_temp, (1 - targetScale) * 2.0f);

				lastRawX = ev.getRawX();
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:

			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.36f) {

					closeUgc(scaleDirection);
					closeMenu();

				} else {
					openMenu(scaleDirection);
				}
			} else {
				if (currentActivityScaleX < 0.94f) {
					openMenu(scaleDirection);
				} else {

					closeUgc(scaleDirection);
					closeMenu();

				}
			}

			break;

		}
		lastRawX = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}

	public int getScreenHeight() {
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void setScaleValue(float scaleValue) {
		this.mScaleValue = scaleValue;
	}

	public interface OnMenuListener {

		/**
		 * the method will call on the finished time of opening menu's animation.
		 */
		public void openMenu();

		/**
		 * the method will call on the finished time of closing menu's animation .
		 */
		public void closeMenu();
	}

}
