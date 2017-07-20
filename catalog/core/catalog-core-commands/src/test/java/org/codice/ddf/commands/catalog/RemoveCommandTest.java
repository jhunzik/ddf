/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.commands.catalog;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ddf.catalog.CatalogFramework;
import ddf.catalog.cache.SolrCacheMBean;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.QueryResponse;

public class RemoveCommandTest extends ConsoleOutputCommon {

    private List<Metacard> metacardList = getMetacardList(5);

    @Before
    public void setup() {
        metacardList = getMetacardList(5);
    }

    @Test
    public void testSingleItemList() throws Exception {
        final SolrCacheMBean mbean = mock(SolrCacheMBean.class);
        RemoveCommand removeCommand = new RemoveCommand() {
            @Override
            protected SolrCacheMBean getCacheProxy() {
                return mbean;
            }
        };

        List<String> ids = new ArrayList<>();
        ids.add(metacardList.get(0)
                .getId());

        removeCommand.ids = ids;
        removeCommand.cache = true;

        removeCommand.executeWithSubject();

        String[] idsArray = new String[ids.size()];
        idsArray = ids.toArray(idsArray);
        verify(mbean, times(1)).removeById(idsArray);
    }

    @Test
    public void testMultipleItemList() throws Exception {
        final SolrCacheMBean mbean = mock(SolrCacheMBean.class);
        RemoveCommand removeCommand = new RemoveCommand() {
            @Override
            protected SolrCacheMBean getCacheProxy() {
                return mbean;
            }
        };

        List<String> ids = new ArrayList<>();
        ids.add(metacardList.get(0)
                .getId());
        ids.add(metacardList.get(1)
                .getId());
        ids.add(metacardList.get(2)
                .getId());

        removeCommand.ids = ids;
        removeCommand.cache = true;

        removeCommand.executeWithSubject();

        String[] idsArray = new String[ids.size()];
        idsArray = ids.toArray(idsArray);
        verify(mbean, times(1)).removeById(idsArray);
    }

    @Test
    public void testOverMaxItemList() throws Exception {
        ConsoleOutput consoleOutput = mock(ConsoleOutput.class);
        int totalResultsToDelete = 100;
        int resultsPerCFQuery = 22;
        int numCFCalls = 5;
        List<String> ids = new ArrayList<>();
        List<Metacard> mcList = getMetacardList(totalResultsToDelete);
        for(int i = 0; i < mcList.size(); i++) {
            ids.add(mcList.get(i).getId());
        }

        RemoveCommand removeCommand = new RemoveCommand();

        final CatalogFramework catalogFramework = mock(CatalogFramework.class);

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(queryResponse.getResults()).thenReturn(getResultList(totalResultsToDelete));
        when(catalogFramework.query(isA(QueryRequest.class))).thenReturn(queryResponse);

        DeleteResponse deleteResponse = mock(DeleteResponse.class);
        when(deleteResponse.getDeletedMetacards()).thenReturn(getMetacardList(resultsPerCFQuery));
        when(catalogFramework.delete(isA(DeleteRequest.class))).thenReturn(deleteResponse);

        removeCommand.catalogFramework = catalogFramework;
        removeCommand.cqlFilter = "title like 'a*'";
        removeCommand.ids = ids;
        removeCommand.cache = false;

        verify(catalogFramework, times(numCFCalls)).delete(isA(DeleteRequest.class));
        assertThat(consoleOutput.getOutput(), containsString("100" + " metacards to remove."));
    }

    /**
     * Tests the {@Link RemoveCommand} when passed
     * a null list of ids
     *
     * @throws Exception
     */
    @Test
    public void testNullList() throws Exception {
        final SolrCacheMBean mbean = mock(SolrCacheMBean.class);
        RemoveCommand removeCommand = new RemoveCommand() {
            @Override
            protected SolrCacheMBean getCacheProxy() {
                return mbean;
            }
        };

        removeCommand.ids = null;

        removeCommand.executeWithSubject();

        assertThat(consoleOutput.getOutput(), containsString("Nothing to remove."));
    }

    private java.util.List<Metacard> getMetacardList(int amount) {
        List<Metacard> metacards = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            String id = UUID.randomUUID()
                    .toString();
            MetacardImpl metacard = new MetacardImpl();
            metacard.setId(id);

            metacards.add(metacard);
        }

        return metacards;
    }

    private java.util.List<Result> getResultList(int amount) {
        java.util.List<Result> results = new ArrayList<>();

        for (int i = 0; i < amount; i++) {

            String id = UUID.randomUUID()
                    .toString();
            MetacardImpl metacard = new MetacardImpl();
            metacard.setId(id);
            Result result = new ResultImpl(metacard);
            results.add(result);

        }

        return results;
    }
}