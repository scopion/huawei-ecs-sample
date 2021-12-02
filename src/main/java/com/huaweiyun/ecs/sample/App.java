package com.huaweiyun.ecs.sample;
import com.huaweicloud.sdk.core.auth.GlobalCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.ecs.v2.EcsClient;
import com.huaweicloud.sdk.ecs.v2.model.*;
import com.huaweicloud.sdk.ecs.v2.region.EcsRegion;
import com.huaweicloud.sdk.eip.v2.EipClient;
import com.huaweicloud.sdk.eip.v2.model.*;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsRequest;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsResponse;
import com.huaweicloud.sdk.iam.v3.model.ProjectResult;
import com.huaweicloud.sdk.iam.v3.region.IamRegion;
import cn.hutool.core.net.NetUtil;
// 用户身份认证
import com.huaweicloud.sdk.core.auth.BasicCredentials;
// 请求异常类
// Http配置
import com.huaweicloud.sdk.core.http.HttpConfig;
// 导入待请求接口的 request 和 response 类

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App{
    public static void main( String[] args )    {

        System.out.println( "Hello World!" );
        NetUtil netUtil = new NetUtil();
        int ecssum = 0;
        int eipsum = 0;
        String ak = "UQ1234123413241234123ML";
        String sk = "sVTa1234123412341234SFbhl";
        //String endpoint = "{your endpoint string}";
        //String domainId = "dd1234123412341234130e9";

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
        //获取project列
        KeystoneListProjectsRequest keystoneListProjectsRequest = new KeystoneListProjectsRequest();
        KeystoneListProjectsResponse keystoneListProjectsResponse = iamClient.keystoneListProjects(keystoneListProjectsRequest);
        List<ProjectResult> projectResultList = keystoneListProjectsResponse.getProjects();
        System.out.println(projectResultList.size());

        //循环打印projectid
        for (com.huaweicloud.sdk.iam.v3.model.ProjectResult projectResult:projectResultList){
            //System.out.println(projectResult.g);
            int ecsregionsum = 0;
            int eipregionsum = 0;
            String projectId = projectResult.getId();
            String region = projectResult.getName();
            System.out.println(projectId + " " +region);
            //建立ECS clinet
            try {
                ICredential auth = new BasicCredentials()
                        .withAk(ak)
                        .withSk(sk);
                EcsClient ecsClient = EcsClient.newBuilder()
                        .withCredential(auth)
                        .withRegion(EcsRegion.valueOf(region))
                        .build();
                EipClient eipClient = EipClient.newBuilder()
                        .withCredential(auth)
                        .withRegion(EcsRegion.valueOf(region))
                        .build();
                NovaListServersDetailsRequest novaListServersDetailsRequest = new NovaListServersDetailsRequest();
                NovaListServersDetailsResponse novaListServersDetailsResponse = ecsClient.novaListServersDetails(novaListServersDetailsRequest);
                List<NovaServer> serverList =novaListServersDetailsResponse.getServers();
                for (com.huaweicloud.sdk.ecs.v2.model.NovaServer novaServer:serverList){
                    String name = novaServer.getName();
                    NovaServer.StatusEnum status = novaServer.getStatus();
                    Collection<List<NovaNetwork>> Addresses = novaServer.getAddresses().values();
                    String privateip = " ";
                    String publicip = " ";
                    Iterator<List<NovaNetwork>> iterator = Addresses.iterator();
                    while (iterator.hasNext()){
                        List<NovaNetwork> addresses = iterator.next();
                        for (NovaNetwork address : addresses) {
                            String ip=address.getAddr();
                            if(netUtil.isInnerIP(ip)){
                                privateip = ip;
                            }else {
                                publicip = ip;
                            }
                        }
                    }
                    System.out.println(name+" "+status+" "+privateip+" "+publicip);
                    System.out.println(publicip+":"+name);

                    ecsregionsum = ecsregionsum+1;
                }
                ListPublicipsRequest listPublicipsRequest = new ListPublicipsRequest();
                ListPublicipsResponse listPublicipsResponse = eipClient.listPublicips(listPublicipsRequest);
                List<PublicipShowResp> publicips =listPublicipsResponse.getPublicips();
                for (com.huaweicloud.sdk.eip.v2.model.PublicipShowResp publicip:publicips){
                    String bandwidthName =publicip.getBandwidthName();
                    PublicipShowResp.BandwidthShareTypeEnum bandwidthShareType = publicip.getBandwidthShareType();
                    String privateIpAddress= publicip.getPrivateIpAddress();
                    String publicIpAddress= publicip.getPublicIpAddress();
                    PublicipShowResp.StatusEnum status = publicip.getStatus();
                    System.out.println(bandwidthName+" "+bandwidthShareType+" "+privateIpAddress+" "+publicIpAddress+status);
                    eipregionsum = eipregionsum+1;
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
            System.out.println(region + " ecs num:" +ecsregionsum);
            ecssum =ecssum+ecsregionsum;
            System.out.println(region + " eip num:" +eipregionsum);
            eipsum = eipsum+eipregionsum;

        }

        System.out.println("all done");
        System.out.println("ecs num:" +ecssum);
        System.out.println("eip num:" +eipsum);
    }
}
