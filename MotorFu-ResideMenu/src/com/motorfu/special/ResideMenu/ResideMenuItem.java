package com.motorfu.special.ResideMenu;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.Icon;
import com.motorfu.special.androidbootstrap.FontAwesomeText;
import com.special.ResideMenu.R;

/**
 * User: special Date: 13-12-10 Time: 下午11:05 Mail: specialcyci@gmail.com
 */
public class ResideMenuItem extends TableRow {

	/** menu item icon */
	private ImageView iv_icon;
	private View view_icon;
	/** menu item title */
	private TextView tv_title;
	private LinearLayout ll_bg;
	private IconicFontDrawable fontDrawable;

	public ResideMenuItem(Context context) {
		super(context);
		initViews(context);
	}

	public ResideMenuItem(Context context, int icon, int title) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_title.setText(title);
	}

	public ResideMenuItem(Context context, int icon, String title) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_title.setText(title);
	}

	public ResideMenuItem(Context context, int icon, String title, int bgId) {
		super(context);
		initViews(context);
		ll_bg.setBackgroundResource(bgId);
		iv_icon.setImageResource(icon);
		tv_title.setText(title);
	}

	public ResideMenuItem(Context context, Icon icon, int iconColor, int iconPadding, String title, int bgId, int direction) {
		super(context);
		initFontViews(context, direction);
		fontDrawable=new IconicFontDrawable(getContext());
		ll_bg.setBackgroundResource(bgId);
		if (icon != null){
			fontDrawable.setIcon(icon);
			fontDrawable.setIconColor(iconColor);
			fontDrawable.setIconPadding(iconPadding);
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			view_icon.setBackgroundDrawable(fontDrawable);
		} else {
			view_icon.setBackground(fontDrawable);
		}
		tv_title.setText(title);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_item, this);
		ll_bg = (LinearLayout) findViewById(R.id.ll_bg);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}

	private void initFontViews(Context context, int direction) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_font_item, this);

		ll_bg = (LinearLayout) findViewById(R.id.ll_bg);
		view_icon = (View) findViewById(R.id.view_icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}

	/**
	 * set the icon color;
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		iv_icon.setImageResource(icon);
	}



	public void setIconColor(int iconColor) {
		fontDrawable.setIconColor(iconColor);
	}

	public void setIconPadding(int iconPadding) {
		fontDrawable.setIconPadding(iconPadding);
	}

	/**
	 * set the title with resource ;
	 * 
	 * @param title
	 */
	public void setTitle(int title) {
		tv_title.setText(title);
	}

	/**
	 * set the title with string;
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setBg(int bgId) {
		ll_bg.setBackgroundResource(bgId);
	}

}
