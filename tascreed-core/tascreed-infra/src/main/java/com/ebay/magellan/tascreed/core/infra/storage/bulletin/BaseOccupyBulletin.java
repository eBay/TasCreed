package com.ebay.magellan.tascreed.core.infra.storage.bulletin;

import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;

public interface BaseOccupyBulletin extends BaseBulletin {

    OccupyInfo occupy(String adoptionKey, String adoptionValue) throws Exception;

    boolean deleteAdoption(OccupyInfo occupyInfo) throws Exception;

    long heartBeat(OccupyInfo occupyInfo) throws TcException;

}
