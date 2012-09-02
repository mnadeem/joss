package nl.t42.openstack.command.account;

import nl.t42.openstack.command.core.*;
import nl.t42.openstack.command.identity.access.Access;
import nl.t42.openstack.model.AccountInformation;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;

import java.io.IOException;

public class AccountInformationCommand extends AbstractSecureCommand<HttpHead, AccountInformation> {

    public static final String X_ACCOUNT_META_PREFIX      = "X-Account-Meta-";
    public static final String X_ACCOUNT_CONTAINER_COUNT  = "X-Account-Container-Count";
    public static final String X_ACCOUNT_OBJECT_COUNT     = "X-Account-Object-Count";
    public static final String X_ACCOUNT_BYTES_USED       = "X-Account-Bytes-Used";

    public AccountInformationCommand(HttpClient httpClient, Access access) {
        super(httpClient, access);
        request.addHeader("Content-type", "application/json");
    }

    @Override
    protected AccountInformation getReturnObject(HttpResponse response) throws IOException {
        AccountInformation info = new AccountInformation();
        for (Header header : response.getAllHeaders()) {
            if (header.getName().startsWith(X_ACCOUNT_META_PREFIX)) {
                info.addMetadata(header.getName().substring(X_ACCOUNT_META_PREFIX.length()), header.getValue());
            }
        }
        info.setContainerCount(Integer.parseInt(response.getHeaders(X_ACCOUNT_CONTAINER_COUNT)[0].getValue()));
        info.setObjectCount(Integer.parseInt(response.getHeaders(X_ACCOUNT_OBJECT_COUNT)[0].getValue()));
        info.setBytesUsed(Long.parseLong(response.getHeaders(X_ACCOUNT_BYTES_USED)[0].getValue()));
        return info;
    }

    @Override
    protected HttpHead createRequest(String url) {
        return new HttpHead(url);
    }

    @Override
    protected HttpStatusChecker[] getStatusCheckers() {
        return new HttpStatusChecker[] {
            new HttpStatusChecker(new HttpStatusMatch(HttpStatus.SC_NO_CONTENT), null)
        };
    }

}
