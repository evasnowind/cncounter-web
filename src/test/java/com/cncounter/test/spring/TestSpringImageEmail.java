package com.cncounter.test.spring;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestSpringImageEmail {
    public static void main(String[] args) throws Exception {
        // 发送带内联图片的MIME邮件
        sendImageEmail();
    }

    // 发送带内联图片的MIME邮件
    public static void sendImageEmail() throws MessagingException, IOException {
        // 邮件发送器
        JavaMailSender mailSender = getJavaMailSender();
        // MIME 邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 辅助类; 明确使用 UTF-8 编码; 否则HTML报中文乱码
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        //
        boolean isHTML = true;
        // 邮件信息
        helper.setFrom("100001@qq.com"); // 发件人邮箱
        helper.setTo("10086@cncounter.cn"); // 收件人邮箱
        helper.setSubject("测试Spring发送内联图片-3"); // 标题
        helper.setText("<h1>测试图片</h1>" +
                "海信电视-InLine:" +
                "<br/>" +
                "<a target='_blank' href='http://www.yuledanao.com/dl/haixin.png'><img src='cid:image1' /></a>" +
                "<br/>" +
                "img-src-http资源:" +
                "<br/>" +
                "<a target='_blank' href='http://www.yuledanao.com/dl/haixin.png'><img src='http://www.yuledanao.com/dl/haixin.png' /></a>" +
                "<br/>" +
                "<br/>" +
                "请点击: <a target='_blank' href='http://www.yuledanao.com/dl/PWA_INTRO.zip'><b>PWA开发简介.zip</b></a>;" +
                " 或者下载附件!", isHTML); // HTML-信息
        // cid - content-ID
        // 增加内联图片:
        // 不作为附件发送
        String imageName1 = "E:/haixin.png";
        InputStream imageInputStream1 = new FileInputStream(imageName1);
        ByteArrayResource imageResource1 =
                new ByteArrayResource(IOUtils.toByteArray(imageInputStream1));
        helper.addInline("image1", imageResource1, "image/png");

        // 增加1个附件; 可以使用多种资源API
        String fileName1 = "E:/PWA开发简介.zip";
        InputStream inputStream1 = new FileInputStream(fileName1);
        //
        // Java Mail 会打开2次 InputStreamResource;
        // 第一次确定编码, 第二次才执行编码。
        ByteArrayResource byteResource1 =
                new ByteArrayResource(IOUtils.toByteArray(inputStream1));
        // 所以不能直接使用 InputStreamResource
        // InputStreamResource attachment1 = new InputStreamResource(inputStream1);
        helper.addAttachment("PWA开发简介.zip", byteResource1);
        try {
            mailSender.send(mimeMessage);
            System.out.println("邮件发送成功!"); // 没有报错就是好消息 :-)
        } catch (MailException ex) {
            System.err.println("发送失败:" + ex.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream1);
        }
    }

    // 获取邮件发送器
    public static JavaMailSender getJavaMailSender() {
        //
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 参考QQ邮箱帮助中心
        mailSender.setHost("smtp.qq.com"); // QQ邮箱smtp发送服务器地址
        //mailSender.setPort(465); // QQ这个端口不可用? 为什么?
        mailSender.setPort(587);// 端口号
        mailSender.setUsername("100001@qq.com"); // 使用你自己的账号
        mailSender.setPassword("usbusbcnzztbsbtob"); // 授权码-发短信获取
        //
        // 相关属性配置, 也可以不修改,使用默认值
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");// 协议
        props.put("mail.smtp.auth", "true");// 登录
        props.put("mail.smtp.starttls.enable", "true");//使用SSL
        //props.put("mail.debug", "true");// 调试信息输出
        //
        return mailSender;
    }
}