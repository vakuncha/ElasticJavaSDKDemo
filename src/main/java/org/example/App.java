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

//        TestListingElasticResources(monitorsClient);

//        TestDeletingElasticResource(monitorsClient);



        MonitorProperties monitorProperties = new MonitorProperties();
          UserInfo userInfo =new UserInfo();
          userInfo.withEmailAddress("sdktestinguser@mpliftrelastic20210901outlo.onmicrosoft.com");
          userInfo.withFirstName("varun");
          userInfo.withLastName("kunchakuri");
          userInfo.withCompanyName("mic");
          CompanyInfo companyInfo = new CompanyInfo();
          companyInfo.withBusiness("nothing");
          companyInfo.withCountry("nothing");
          companyInfo.withDomain("nothing");
          companyInfo.withState("nothing");
          userInfo.withCompanyInfo(companyInfo);

         monitorProperties.withUserInfo(userInfo);
         IdentityProperties identityProperties = new IdentityProperties();
         identityProperties.withType(ManagedIdentityTypes.fromString("None"));
        ElasticMonitorResourceInner elasticMonitorResourceInner = new ElasticMonitorResourceInner();
        elasticMonitorResourceInner.withSku(new ResourceSku().withName("ess-monthly-consumption_Monthly"));
        elasticMonitorResourceInner.withLocation("westus2");
        elasticMonitorResourceInner.withProperties(monitorProperties);
        elasticMonitorResourceInner.withIdentity(identityProperties);

        TestCreatingElasticResource(monitorsClient,monitorProperties);

    }

    private static void TestListingElasticResources(Monitors monitorsClient) {
        Iterator<ElasticMonitorResource> elasticIterator = monitorsClient.listByResourceGroup("vakuncha-test-rg").iterator();

        while (elasticIterator.hasNext()){
            System.out.println("Elastic Resource Name: "+elasticIterator.next().name());
        }
    }
    private static void TestDeletingElasticResource(Monitors monitorsClient) {
        monitorsClient.delete("vakuncha-test-rg", "afterrotationandrestart", Context.NONE);
    }

    private static void TestCreatingElasticResource(Monitors monitorsClient,MonitorProperties monitorProperties) {
      monitorsClient
                .define("sdkmonitorresource")
                .withRegion("eastus2euap")
                .withExistingResourceGroup("vakuncha-test-rg")
                .withProperties(monitorProperties)
                .withSku(new ResourceSku().withName("ess-monthly-consumption_Monthly"))
                .create();
    }
}
