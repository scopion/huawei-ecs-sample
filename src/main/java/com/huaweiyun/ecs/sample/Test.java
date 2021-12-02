package com.huaweiyun.ecs.sample;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.GlobalCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.ecs.v2.EcsClient;
import com.huaweicloud.sdk.ecs.v2.model.NovaListServersDetailsRequest;
import com.huaweicloud.sdk.ecs.v2.model.NovaListServersDetailsResponse;
import com.huaweicloud.sdk.ecs.v2.model.NovaNetwork;
import com.huaweicloud.sdk.ecs.v2.model.NovaServer;
import com.huaweicloud.sdk.ecs.v2.region.EcsRegion;
import com.huaweicloud.sdk.eip.v2.EipClient;
import com.huaweicloud.sdk.eip.v2.model.ListPublicipsRequest;
import com.huaweicloud.sdk.eip.v2.model.ListPublicipsResponse;
import com.huaweicloud.sdk.eip.v2.model.PublicipShowResp;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsRequest;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsResponse;
import com.huaweicloud.sdk.iam.v3.model.ProjectResult;
import com.huaweicloud.sdk.iam.v3.region.IamRegion;

import java.util.Collection;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Test {
    public static void main( String[] args )    {

        System.out.println( "Hello World!" );
        int ecssum = 0;
        int eipsum = 0;
//        String ak = "FL123412341DYHQ";
//        String sk = "nRpng3O1243123412341ptn";
        String ak = "UQ9123412412431UML";
        String sk = "sVT1234123412341234SSFbhl";
        //String endpoint = "{your endpoint string}";
        //String domainId = "ddbe8123412341234123412410e9";

        // 配置客户端属性
        HttpConfig config = HttpConfig.getDefaultHttpConfig();
        config.withIgnoreSSLVerification(true);
        //// 根据需要配置网络代理，网络代理默认的协议为 `http` 协议
        //config.withProxyHost("proxy.huaweicloud.com")
        //    .withProxyPort(8080)
        //    .withProxyUsername("test")
        //    .withProxyPassword("test");

        // Global级服务
        GlobalCredentials globalCredentials = new GlobalCredentials()
                .withAk(ak)
                .withSk(sk);
        // 初始化指定云服务的客户端 {Service}Client ，以初始化 Global 级服务 IAM 的 IamClient 为例
        IamClient iamClient = IamClient.newBuilder()
                .withHttpConfig(config)
                .withCredential(globalCredentials)
                .withRegion(IamRegion.CN_NORTH_4)
                .build();
            try {
                ICredential auth = new BasicCredentials()
                        .withAk(ak)
                        .withSk(sk);

                EipClient eipClient = EipClient.newBuilder()
                        .withCredential(auth)
                        .withRegion(EcsRegion.valueOf("cn-east-3"))
                        .build();

                ListPublicipsRequest listPublicipsRequest = new ListPublicipsRequest();
                ListPublicipsResponse listPublicipsResponse = eipClient.listPublicips(listPublicipsRequest);
                List<PublicipShowResp> publicips =listPublicipsResponse.getPublicips();
                for (PublicipShowResp publicip:publicips){
                    String bandwidthName =publicip.getBandwidthName();
                    PublicipShowResp.BandwidthShareTypeEnum bandwidthShareType = publicip.getBandwidthShareType();
                    String privateIpAddress= publicip.getPrivateIpAddress();
                    String publicIpAddress= publicip.getPublicIpAddress();
                    PublicipShowResp.StatusEnum status = publicip.getStatus();
                    System.out.println(bandwidthName);
                    System.out.println(bandwidthShareType);
                    System.out.println(privateIpAddress);
                    System.out.println(publicIpAddress);
                    System.out.println(status);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


    }
}
