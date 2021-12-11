/*
 * Copyright 2021-2021 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.engine.drive.test.internal.cog;

import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;

import io.aklivity.zilla.engine.drive.cog.vault.BindingVault;
import io.aklivity.zilla.engine.drive.config.Vault;

final class TestVault implements BindingVault
{
    TestVault(
        Vault vault)
    {
    }

    @Override
    public PrivateKeyEntry key(
        String name)
    {
        return null;
    }

    @Override
    public TrustedCertificateEntry certificate(
        String name)
    {
        return null;
    }

}
