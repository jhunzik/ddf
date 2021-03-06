/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.catalog.async.data

import org.codice.ddf.catalog.async.data.api.internal.ProcessResource
import org.codice.ddf.catalog.async.data.impl.ProcessResourceImpl
import spock.lang.Specification

class ProcessResourceImplSpec extends Specification {

    static final ID = 'id'

    def inputStream = Mock(InputStream)

    static final MIME_TYPE = "mimeType"

    static final RESOURCE_NAME = "test"

    static final SIZE = 1

    static final QUALIFIER = "thumbnail"

    static final RESOURCE_URI = ProcessResourceImpl.CONTENT_SCHEME + ":" + ID

    def 'process resource no qualifier'() {
        when:
        def processResource = new ProcessResourceImpl(ID, inputStream, MIME_TYPE, RESOURCE_NAME, SIZE)
        processResource.markAsModified()

        then:
        processResource.getQualifier() == ''

        processResource.isModified()
        processResource.inputStream == inputStream
        processResource.getUri().toString() == RESOURCE_URI
        processResource.getUri().getSchemeSpecificPart() == ID
        processResource.getMimeType() == MIME_TYPE
        processResource.getSize() == SIZE
    }

    def 'process resource unknown size'() {
        when:
        def processResource = new ProcessResourceImpl(ID, inputStream, MIME_TYPE, RESOURCE_NAME)
        processResource.markAsModified()

        then:
        processResource.getSize() == ProcessResource.UNKNOWN_SIZE

        processResource.getQualifier() == ''
        processResource.isModified()
        processResource.inputStream == inputStream
        processResource.getUri().toString() == RESOURCE_URI
        processResource.getUri().getSchemeSpecificPart() == ID
        processResource.getMimeType() == MIME_TYPE
    }

    def 'process resource no qualifier and not modified'() {
        when:
        def processResource = new ProcessResourceImpl(ID, inputStream, MIME_TYPE, RESOURCE_NAME, SIZE)

        then:
        processResource.getQualifier() == ''
        !processResource.isModified()

        processResource.inputStream == inputStream
        processResource.getUri().toString() == RESOURCE_URI
        processResource.getUri().getSchemeSpecificPart() == ID
        processResource.getMimeType() == MIME_TYPE
        processResource.getSize() == SIZE
    }

    def 'test process resource is qualified'() {
        when:
        def processResource = new ProcessResourceImpl(ID, inputStream, MIME_TYPE, RESOURCE_NAME, SIZE, QUALIFIER)
        processResource.markAsModified()

        then:
        processResource.getQualifier() == QUALIFIER
        processResource.isModified()
        processResource.getUri().toString() == RESOURCE_URI + "#" + QUALIFIER
    }

    def 'test ProcessResourceImpl(String, InputStream, String, String, Int, String, Boolean)'() {
        when:
        def stream = Mock(InputStream)
        def processResource = new ProcessResourceImpl(ID, stream, MIME_TYPE, RESOURCE_NAME, SIZE, QUALIFIER)

        then:
        assert processResource.getQualifier() == QUALIFIER
        assert !processResource.isModified()
        assert processResource.getName() == RESOURCE_NAME
        assert processResource.getMimeType() == MIME_TYPE
        assert processResource.getInputStream() == stream
        assert processResource.getSize() == SIZE
        assert processResource.getUri().toString() == RESOURCE_URI + "#" + QUALIFIER
    }

    def 'test process resource ProcessResourceImpl null qualifier'() {
        when:
        def stream = Mock(InputStream)
        def processResource = new ProcessResourceImpl(ID, stream, MIME_TYPE, RESOURCE_NAME, SIZE, null)

        then:
        assert processResource.getUri().toString() == RESOURCE_URI
        assert processResource.getQualifier() == ''

        assert !processResource.isModified()
        assert processResource.getName() == RESOURCE_NAME
        assert processResource.getMimeType() == MIME_TYPE
        assert processResource.getInputStream() == stream
        assert processResource.getSize() == SIZE
    }

    def 'test process resource ProcessResourceImpl null mimeType and null fileName'() {
        when:
        def stream = Mock(InputStream)
        def processResource = new ProcessResourceImpl(ID, stream, null, null, SIZE, QUALIFIER)

        then:
        assert processResource.getName() == ProcessResourceImpl.DEFAULT_NAME
        assert processResource.getMimeType() == ProcessResourceImpl.DEFAULT_MIME_TYPE

        assert processResource.getQualifier() == QUALIFIER
        assert !processResource.isModified()
        assert processResource.getInputStream() == stream
        assert processResource.getSize() == SIZE
    }

    def 'test process resource IllegalArgumentException with blank or null id'() {
        when:
        new ProcessResourceImpl(id, inputStream, MIME_TYPE, RESOURCE_NAME, SIZE, QUALIFIER)

        then:
        thrown IllegalArgumentException

        where:
        id << ["", null]
    }

    def 'test process resource IllegalArgumentException with invalid size'() {
        when:
        new ProcessResourceImpl(ID, inputStream, MIME_TYPE, RESOURCE_NAME, size, QUALIFIER)

        then:
        thrown IllegalArgumentException

        where:
        size | _
        0    | _
        -2   | _
    }

    def 'test process resource IllegalArgumentException with null inputStream'() {
        when:
        new ProcessResourceImpl(ID, null, MIME_TYPE, RESOURCE_NAME, SIZE, QUALIFIER)

        then:
        thrown IllegalArgumentException
    }
}