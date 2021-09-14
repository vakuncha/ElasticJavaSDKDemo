package org.example;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.elastic.ElasticManager;
import com.azure.resourcemanager.elastic.fluent.models.ElasticMonitorResourceInner;
import com.azure.resourcemanager.elastic.models.*;

import java.util.Iterator;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        ElasticManager elasticManager = ElasticManager
                .authenticate(credential, profile);
        Monitors monitorsClient = elasticManager.monitors();
   
        TestListingElasticResources(monitorsClient);

        TestDeletingElasticResource(monitorsClient);

        MonitorProperties monitorProperties = new MonitorProperties();
          UserInfo userInfo =new UserInfo();
        userInfo.withFirstName("varun");
        userInfo.withLastName("kunchakuri");
        userInfo.withCompanyName("microsoft");
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.withBusiness("cloud");
        companyInfo.withCountry("india");
        companyInfo.withDomain("software");
        companyInfo.withState("andhrapradesh");
        userInfo.withCompanyInfo(companyInfo);
        userInfo.withEmailAddress("sdkdemo@mpliftrelastic20210901outlo.onmicrosoft.com");
         monitorProperties.withUserInfo(userInfo);

        String resourceName = TestCreatingElasticResource(monitorsClient,monitorProperties);

        TagRules tagRulesClient = elasticManager.tagRules();

        TestingUpdateTagRules(tagRulesClient,"vakuncha-test-rg",resourceName);

    }

    private static void TestingUpdateTagRules(TagRules tagRulesClient,String resourceGroup, String resourceName) {
        System.out.println("Updating Tag Rules for Resource: "+resourceName);
        MonitoringTagRulesProperties tagRules = new MonitoringTagRulesProperties();
        tagRules.withLogRules(new LogRules().withSendActivityLogs(true).withSendSubscriptionLogs(true));
        tagRulesClient.define("default")
        .withExistingMonitor(resourceGroup, resourceName)
                .withProperties(tagRules).create();
    }

    private static void TestListingElasticResources(Monitors monitorsClient) {
        Iterator<ElasticMonitorResource> elasticIterator = monitorsClient.listByResourceGroup("vakuncha-test-rg").iterator();

        System.out.println("Listing Elastic Resources");
        while (elasticIterator.hasNext()){
            System.out.println("Elastic Resource Name: "+elasticIterator.next().name());
        }
    }
    private static void TestDeletingElasticResource(Monitors monitorsClient) {

        Iterator<ElasticMonitorResource> elasticIterator = monitorsClient.listByResourceGroup("vakuncha-test-rg").iterator();

        if (elasticIterator.hasNext()){
            String resourceName = elasticIterator.next().name();
            System.out.println("Deleting Elastic Resource: "+resourceName);
            monitorsClient.delete("vakuncha-test-rg", resourceName, Context.NONE);
        }
    }

    private static String TestCreatingElasticResource(Monitors monitorsClient,MonitorProperties monitorProperties) {
      String resourceName =   "sdkmonitorresource-"+System.currentTimeMillis();
      System.out.println("Creating Elastic Resource: "+resourceName);
      monitorsClient
                .define(resourceName)
                .withRegion("eastus2euap")
                .withExistingResourceGroup("vakuncha-test-rg")
                .withProperties(monitorProperties)
                .withSku(new ResourceSku().withName("ess-monthly-consumption_Monthly"))
                .create();
      return resourceName;
    }
}
