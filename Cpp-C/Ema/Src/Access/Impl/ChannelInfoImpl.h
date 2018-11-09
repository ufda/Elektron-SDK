/*|-----------------------------------------------------------------------------
 *|            This source code is provided under the Apache 2.0 license      --
 *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
 *|                See the project's LICENSE.md for details.                  --
 *|           Copyright Thomson Reuters 2018. All rights reserved.            --
 *|-----------------------------------------------------------------------------
 */

#ifndef __thomsonreuters_ema_access_ChannelInfoImpl_h
#define __thomsonreuters_ema_access_ChannelInfoImpl_h

#include "ItemCallbackClient.h"
#include "OmmBaseImplMap.h"

namespace thomsonreuters {
namespace ema {
namespace access {

  void getChannelInformation(const RsslReactorChannel*, const RsslChannel*, ChannelInformation&);
  void getChannelInformationImpl(const RsslReactorChannel*, OmmCommonImpl::ImplementationType,
								 ChannelInformation&);
}
}
}
#endif
