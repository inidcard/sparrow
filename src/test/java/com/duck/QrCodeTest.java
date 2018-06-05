package com.duck;

import org.junit.Test;

import com.zero.tree.qrcode.QrCodeBase;

public class QrCodeTest {
	
	@Test
	public void qrCode01() {
		
		String content = "http://www.163.com";
		String imgPath = "d:/tmp/qrcode_163_2.png";
		String imgType = "png";
		int size = 10;
		String charset = "utf-8";

		try {
			QrCodeBase.encoderQRCode(content, imgPath, imgType, size, charset);
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	@Test
	public void decodeQrCode01() {
		String imgPath = "d:/tmp/qrcode_163_2.png";
		String charset = "utf-8";
		
		try {
			String content = QrCodeBase.decoderQRCode(imgPath, charset);
			System.out.println("===: " + content);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
