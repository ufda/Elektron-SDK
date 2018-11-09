///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright Thomson Reuters 2018. All rights reserved.            --
///*|-----------------------------------------------------------------------------

package com.thomsonreuters.ema.examples.training.niprovider.series100.example170__MarketPrice__ChannelInfo;

import com.thomsonreuters.ema.access.ChannelInformation;
import com.thomsonreuters.ema.access.EmaFactory;
import com.thomsonreuters.ema.access.FieldList;
import com.thomsonreuters.ema.access.GenericMsg;
import com.thomsonreuters.ema.access.Msg;
import com.thomsonreuters.ema.access.OmmException;
import com.thomsonreuters.ema.access.OmmNiProviderConfig;
import com.thomsonreuters.ema.access.OmmProvider;
import com.thomsonreuters.ema.access.OmmProviderClient;
import com.thomsonreuters.ema.access.OmmProviderEvent;
import com.thomsonreuters.ema.access.OmmReal;
import com.thomsonreuters.ema.access.OmmState;
import com.thomsonreuters.ema.access.PostMsg;
import com.thomsonreuters.ema.access.RefreshMsg;
import com.thomsonreuters.ema.access.ReqMsg;
import com.thomsonreuters.ema.access.StatusMsg;
import com.thomsonreuters.ema.rdm.EmaRdm;

class AppClient implements OmmProviderClient
{
	public void onRefreshMsg( RefreshMsg refreshMsg, OmmProviderEvent event )
	{
		System.out.println( "event channel info (refresh)\n" + event.channelInformation() + "\n" + refreshMsg );
	}
	public void onAllMsg( Msg msg, OmmProviderEvent event ) {}
	public void onGenericMsg( GenericMsg genericMsg, OmmProviderEvent event ) {}
	public void onPostMsg( PostMsg postMsg, OmmProviderEvent event ) {}
	public void onReqMsg( ReqMsg reqMsg, OmmProviderEvent event ) {}
	public void onReissue( ReqMsg reqMsg, OmmProviderEvent event ) {}
	public void onClose( ReqMsg reqMsg, OmmProviderEvent event ) 
	{
		System.out.println( "event channel info (close)\n" + event.channelInformation() + "\n" + reqMsg );
	}
	public void onStatusMsg(StatusMsg statusMsg, OmmProviderEvent event) 
	{
		System.out.println( "event channel info (status)\n" + event.channelInformation() + "\n" + statusMsg );
	}
}

public class NiProvider {

	public static void main(String[] args)
	{
		OmmProvider provider = null;
		try
		{
			AppClient appClient = new AppClient();
			OmmNiProviderConfig config = EmaFactory.createOmmNiProviderConfig("EmaConfig.xml");
			ChannelInformation ci = EmaFactory.createChannelInformation();
			
			provider = EmaFactory.createOmmProvider(config.username("user"));
			provider.channelInformation(ci);
			System.out.println("channel information (niprovider): " + ci);
			
			provider.registerClient(EmaFactory.createReqMsg().domainType(EmaRdm.MMT_LOGIN), appClient);
			
			long itemHandle = 5;
			
			FieldList fieldList = EmaFactory.createFieldList();
			
			fieldList.add( EmaFactory.createFieldEntry().real(22, 3990, OmmReal.MagnitudeType.EXPONENT_NEG_2));
			fieldList.add( EmaFactory.createFieldEntry().real(25, 3994, OmmReal.MagnitudeType.EXPONENT_NEG_2));
			fieldList.add( EmaFactory.createFieldEntry().real(30, 9,  OmmReal.MagnitudeType.EXPONENT_0));
			fieldList.add( EmaFactory.createFieldEntry().real(31, 19, OmmReal.MagnitudeType.EXPONENT_0));
			
			provider.submit( EmaFactory.createRefreshMsg().serviceName("NI_PUB").name("IBM.N")
					.state(OmmState.StreamState.OPEN, OmmState.DataState.OK, OmmState.StatusCode.NONE, "UnSolicited Refresh Completed")
					.payload(fieldList).complete(true), itemHandle);
			
			Thread.sleep(1000);
			
			for( int i = 0; i < 60; i++ )
			{
				fieldList.clear();
				fieldList.add(EmaFactory.createFieldEntry().real(22, 3991 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
				fieldList.add(EmaFactory.createFieldEntry().real(30, 10 + i, OmmReal.MagnitudeType.EXPONENT_0));
				
				try {
					provider.submit( EmaFactory.createUpdateMsg().serviceName("DIRECT_FEED").name("IBM.N").payload( fieldList ), itemHandle );
				}
				catch (OmmException e) {  // allows one to stop/start the adh and see the status showing channel going down/up
					System.out.println("submit update message threw exception " + e.getClass().getCanonicalName());
				}
				
				Thread.sleep(1000);
			}
		} 
		catch (InterruptedException | OmmException excp)
		{
			System.out.println(excp.getMessage());
		}
		finally 
		{
			if (provider != null) provider.uninitialize();
		}
	}
}
