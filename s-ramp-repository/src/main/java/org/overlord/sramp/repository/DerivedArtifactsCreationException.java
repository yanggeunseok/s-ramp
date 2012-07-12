/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.sramp.repository;

public class DerivedArtifactsCreationException extends Exception {

    private static final long serialVersionUID = -1205817784608428279L;

    public DerivedArtifactsCreationException() {
        super();
    }

    public DerivedArtifactsCreationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DerivedArtifactsCreationException(String arg0) {
        super(arg0);
    }

    public DerivedArtifactsCreationException(Throwable arg0) {
        super(arg0);
    }

}