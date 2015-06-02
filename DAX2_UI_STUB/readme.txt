DsUI.apk needs to be signed in order to get the UserId permission. You can use the following command on your build platform to get DsUI.apk signed.
java -jar signapk.jar¡¡platform.x509.pem platform.pk8 Ds.apk Ds_Signed.apk
Three files are required to get Ds.apk signed. And they can be get from your current build: 
1) signapk.jar, which can be got under out/host/linux-x86/framework/signapk.jar
2) platform.x509.pem, which can be got under build/target/product/security/platform.x509.pem
3) platform.pk8, which can be got under build/target/product/security/platform.pk8
