package soulib.lib3d;

import java.awt.Color;

import soulib.windowLib.CommonGraphics;

public interface CommonGraphics3D extends CommonGraphics{

	/**
	 * 指定した座標、高さ、幅、奥行き、角度、個別の色<br>
	 * で面を塗りつぶした箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * @param c 色
	 * */
	public void fillBox(float x,float y,float z,float w,float h,float d,float[] angle,Color[] c);
	/**
	 * 指定した座標、高さ、幅、奥行き、角度、共通の色<br>
	 * で面を塗りつぶした箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * @param c 全ての面の色
	 * */
	public void fillBox(float x,float y,float z,float w,float h,float d,float[] angle,Color c);
	/**
	 * 指定した座標、高さ、幅、奥行き、角度、現在の色<br>
	 * で面を塗りつぶした箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * */
	public void fillBox(float x,float y,float z,float w,float h,float d,float[] angle);
	/**
	 * 指定した座標、高さ、幅、奥行き、固定の角度、現在の色<br>
	 * で面を塗りつぶした箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * */
	public void fillBox(float x,float y,float z,float w,float h,float d);
	/**
	 * 指定した座標、高さ、幅、奥行き、角度、個別の色<br>
	 * で面を塗りつぶした箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * @param c 色
	 * */
	public void drawBox(float x,float y,float z,float w,float h,float d,float[] angle,Color[] c);
	/**
	 * 指定した座標、高さ、幅、奥行き、角度、共通の色<br>
	 * で枠だけの箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * @param c 全ての面の色
	 * */
	public void drawBox(float x,float y,float z,float w,float h,float d,float[] angle,Color c);
	/**
	 * 指定した座標、高さ、幅、奥行き、角度、現在の色<br>
	 * で枠だけの箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * @param angle 角度
	 * */
	public void drawBox(float x,float y,float z,float w,float h,float d,float[] angle);
	/**
	 * 指定した座標、高さ、幅、奥行き、固定の角度、現在の色<br>
	 * で枠だけの箱を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param w 幅
	 * @param h 高さ
	 * @param d 奥行き
	 * */
	public void drawBox(float x,float y,float z,float w,float h,float d);
}
