package com.ebay.magellan.tumbler.core.infra.storage.bulletin;

import com.ebay.magellan.tumbler.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;

public interface BaseOccupyBulletin extends BaseBulletin {

    OccupyInfo occupy(String adoptionKey, String adoptionValue) throws Exception;

    boolean deleteAdoption(OccupyInfo occupyInfo) throws Exception;

    long heartBeat(OccupyInfo occupyInfo) throws TumblerException;

}
