package nl.t42.openstack.command.account;

import nl.t42.openstack.command.container.ContainerInformationCommand;
import nl.t42.openstack.command.core.BaseCommandTest;
import nl.t42.openstack.command.core.CommandExceptionError;
import nl.t42.openstack.model.AccountInformation;
import nl.t42.openstack.model.Container;
import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static nl.t42.openstack.command.account.AccountInformationCommand.*;
import static org.mockito.Mockito.when;

public class AccountInformationCommandTest extends BaseCommandTest {

    @Before
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    public void getInfoSuccess() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(204);
        List<Header> headers = new ArrayList<Header>();
        prepareHeader(response, X_ACCOUNT_META_PREFIX + "Description", "Photo album", headers);
        prepareHeader(response, X_ACCOUNT_META_PREFIX + "Year", "1984", headers);
        prepareHeader(response, X_ACCOUNT_CONTAINER_COUNT, "7", headers);
        prepareHeader(response, X_ACCOUNT_OBJECT_COUNT, "123", headers);
        prepareHeader(response, X_ACCOUNT_BYTES_USED, "654321", headers);
        when(response.getAllHeaders()).thenReturn(headers.toArray(new Header[headers.size()]));
        AccountInformation info = new AccountInformationCommand(httpClient, defaultAccess).execute();
        assertEquals(7, info.getContainerCount());
        assertEquals(123, info.getObjectCount());
        assertEquals(654321, info.getBytesUsed());
    }

    @Test
    public void unknownError() throws IOException {
        checkForError(500, new ContainerInformationCommand(httpClient, defaultAccess, new Container("containerName")), CommandExceptionError.UNKNOWN);
    }
}
