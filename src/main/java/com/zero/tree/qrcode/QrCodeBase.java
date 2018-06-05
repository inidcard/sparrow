package com.zero.tree.qrcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.swetake.util.Qrcode;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

public class QrCodeBase {
	
	/**
	 * @创建人 zero
	 * @创建时间 2018年6月1日 下午7:12:21
	 * @说明  生成二维码(QRCode)图片
	 * @备注
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 * @return void
	 * @throws Exception 
	 */
	public static void encoderQRCode(String content, String imgPath, String imgType,
			int size, String charset) throws Exception {
		try {
			// 二维码级别 (1-40)
			if (size < 1 || size > 40) {
				// 默认为 7
				size = 7;
			}
			BufferedImage bufImg = qRCodeCommon(content, size, charset);
			File imgFile = new File(imgPath);
			// 生成二维码QRCode图片
			if (StringUtils.isBlank(imgType)) {
				imgType = "png";
			}
			ImageIO.write(bufImg, imgType, imgFile);
		} catch (Exception e) {
			throw new Exception("function encoderQRCode: ", e);
		}
	}

	/**
	 * 生成二维码(QRCode)图片
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 * @throws Exception 
	 * 未测试呢
	 */
	public static void encoderQRCode(String content, OutputStream output, String imgType,
			int size, String charset) throws Exception {
		try {
			BufferedImage bufImg = qRCodeCommon(content, size, charset);
			// 生成二维码QRCode图片
			if (StringUtils.isBlank(imgType)) {
				imgType = "png";
			}
			ImageIO.write(bufImg, imgType, output);
		} catch (Exception e) {
			throw new Exception("function encoderQRCode: ", e);
		}
	}
	
	/**
	 * @创建人 zero
	 * @创建时间 2018年6月1日 下午7:12:46
	 * @说明 生成二维码(QRCode)图片的公共方法
	 * @备注
	 * @param content 存储内容
	 * @param size 二维码尺寸
	 * @param charset 字符集 -默认utf-8
	 * @return BufferedImage
	 * @throws Exception
	 */
	private static BufferedImage qRCodeCommon(String content, int size, String charset) throws Exception {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);

			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			if (!StringUtils.isBlank(charset)) {
				contentBytes = content.getBytes(charset);
			}
			
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();

			// 设置背景颜色 -> WHITE
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);
			// 设定图像颜色 -> BLACK
			gs.setColor(Color.BLACK);

			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;

			// 内容最大长度  如果内容不全增加级别 （1 - 40）
            int lengthTmp = (21 + 4*(size - 1));
			int contentLength = (lengthTmp * lengthTmp - (8*9*3 + 8 + 1)) / 8;

			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < contentLength) {
			    boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = " 
			            + contentBytes.length + " not in [0, " + contentLength + "].");
			}

			gs.dispose();
			bufImg.flush();

		} catch (Exception e) {
			throw new Exception("生成二维码时异常: ", e);
		}
		return bufImg;
	}
	
	/**
	 * @创建人 zero
	 * @创建时间 2018年6月1日 下午7:37:44
	 * @说明 解析二维码(QRCODE)图片
	 * @备注
	 * @param imgPath
	 * @return String
	 * @throws Exception 
	 */
	public static String decoderQRCode(String imgPath, String charset) throws Exception {
		// QRCode 二维码图片的文件
		File imageFile = new File(imgPath);
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(imageFile);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new DimensionQRCodeImage(bufImg)), charset); 
		} catch (IOException e) {
			throw new Exception(e);
		} catch (DecodingFailedException dfe) {
			throw new Exception(dfe);
		}
		return content;
	}
	
	/**
	 * 解析二维码（QRCode）
	 * @param input 输入流
	 * @return
	 * 未测试
	 */
	public String decoderQRCode(InputStream input) {
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(input);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new DimensionQRCodeImage(bufImg)), "utf-8"); 
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (DecodingFailedException dfe) {
			System.out.println("Error: " + dfe.getMessage());
			dfe.printStackTrace();
		}
		return content;
	}

}
