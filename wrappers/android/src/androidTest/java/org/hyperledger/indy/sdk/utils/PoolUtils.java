package org.hyperledger.indy.sdk.utils;

import org.apache.commons.io.FileUtils;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.ledger.Ledger;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.pool.PoolJSONParameters;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PoolUtils {

	private static final String DEFAULT_POOL_NAME = "default_pool";
	public static final int TEST_TIMEOUT_FOR_REQUEST_ENSURE = 200_000;
	private static final int RESUBMIT_REQUEST_TIMEOUT = 5_000;
	private static final int RESUBMIT_REQUEST_CNT = 3;

	public static File createGenesisTxnFile(String filename) throws IOException {
		String path = EnvironmentUtils.getTmpPath(filename);

		File file = new File(path);

		FileUtils.forceMkdirParent(file);

		writeTransactions(file);
		return file;
	}

	public static void writeTransactions(File file) throws IOException {
////		String testPoolIp = EnvironmentUtils.getTestPoolIP();
		String testPoolIp = "pool";
//
//		// this data and pool_transactions_genesis must have the same data and IP addresses
		String[] defaultTxns = new String[]{
				String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node1\",\"blskey\":\"4N8aUNHSgjQVgkpm8nhNEfDf6txHznoYREg9kirmJrkivgL4oSEimFF6nsQ6M41QvhM2Z33nves5vfSn9n1UwNFJBYtWVnHYMATn76vLuL3zU88KyeAYcHfsih3He6UHcXDxcaecHVz6jhCYz1P2UZn2bDVruL5wXpehgBfBaLKm3Ba\",\"blskey_pop\":\"RahHYiCvoNCtPTrVtP7nMC5eTYrsUA8WjXbdhNc8debh1agE9bGiJxWBXYNFbnJXoXhWFMvyqhqhRoq737YQemH5ik9oL7R4NTTCz2LEZhkgLJzB3QRQqJyBNyv7acbdHrAT8nQ9UkLbaVL9NBpnWXBTw4LEMePaSHEw66RzPNdAX1\",\"client_ip\":\"%s\",\"client_port\":9702,\"node_ip\":\"%s\",\"node_port\":9701,\"services\":[\"VALIDATOR\"]},\"dest\":\"Gw6pDLhcBcoQesN72qfotTgFa7cbuqZpkX3Xo6pLhPhv\"},\"metadata\":{\"from\":\"Th7MpTaRZVRYnPiabds81Y\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":1,\"txnId\":\"fea82e10e894419fe2bea7d96296a6d46f50f93f9eeda954ec461b2ed2950b62\"},\"ver\":\"1\"}", testPoolIp, testPoolIp),
				String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node2\",\"blskey\":\"37rAPpXVoxzKhz7d9gkUe52XuXryuLXoM6P6LbWDB7LSbG62Lsb33sfG7zqS8TK1MXwuCHj1FKNzVpsnafmqLG1vXN88rt38mNFs9TENzm4QHdBzsvCuoBnPH7rpYYDo9DZNJePaDvRvqJKByCabubJz3XXKbEeshzpz4Ma5QYpJqjk\",\"blskey_pop\":\"Qr658mWZ2YC8JXGXwMDQTzuZCWF7NK9EwxphGmcBvCh6ybUuLxbG65nsX4JvD4SPNtkJ2w9ug1yLTj6fgmuDg41TgECXjLCij3RMsV8CwewBVgVN67wsA45DFWvqvLtu4rjNnE9JbdFTc1Z4WCPA3Xan44K1HoHAq9EVeaRYs8zoF5\",\"client_ip\":\"%s\",\"client_port\":9704,\"node_ip\":\"%s\",\"node_port\":9703,\"services\":[\"VALIDATOR\"]},\"dest\":\"8ECVSk179mjsjKRLWiQtssMLgp6EPhWXtaYyStWPSGAb\"},\"metadata\":{\"from\":\"EbP4aYNeTHL6q385GuVpRV\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":2,\"txnId\":\"1ac8aece2a18ced660fef8694b61aac3af08ba875ce3026a160acbc3a3af35fc\"},\"ver\":\"1\"}", testPoolIp, testPoolIp),
				String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node3\",\"blskey\":\"3WFpdbg7C5cnLYZwFZevJqhubkFALBfCBBok15GdrKMUhUjGsk3jV6QKj6MZgEubF7oqCafxNdkm7eswgA4sdKTRc82tLGzZBd6vNqU8dupzup6uYUf32KTHTPQbuUM8Yk4QFXjEf2Usu2TJcNkdgpyeUSX42u5LqdDDpNSWUK5deC5\",\"blskey_pop\":\"QwDeb2CkNSx6r8QC8vGQK3GRv7Yndn84TGNijX8YXHPiagXajyfTjoR87rXUu4G4QLk2cF8NNyqWiYMus1623dELWwx57rLCFqGh7N4ZRbGDRP4fnVcaKg1BcUxQ866Ven4gw8y4N56S5HzxXNBZtLYmhGHvDtk6PFkFwCvxYrNYjh\",\"client_ip\":\"%s\",\"client_port\":9706,\"node_ip\":\"%s\",\"node_port\":9705,\"services\":[\"VALIDATOR\"]},\"dest\":\"DKVxG2fXXTU8yT5N7hGEbXB3dfdAnYv1JczDUHpmDxya\"},\"metadata\":{\"from\":\"4cU41vWW82ArfxJxHkzXPG\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":3,\"txnId\":\"7e9f355dffa78ed24668f0e0e369fd8c224076571c51e2ea8be5f26479edebe4\"},\"ver\":\"1\"}", testPoolIp, testPoolIp),
				String.format("{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node4\",\"blskey\":\"2zN3bHM1m4rLz54MJHYSwvqzPchYp8jkHswveCLAEJVcX6Mm1wHQD1SkPYMzUDTZvWvhuE6VNAkK3KxVeEmsanSmvjVkReDeBEMxeDaayjcZjFGPydyey1qxBHmTvAnBKoPydvuTAqx5f7YNNRAdeLmUi99gERUU7TD8KfAa6MpQ9bw\",\"blskey_pop\":\"RPLagxaR5xdimFzwmzYnz4ZhWtYQEj8iR5ZU53T2gitPCyCHQneUn2Huc4oeLd2B2HzkGnjAff4hWTJT6C7qHYB1Mv2wU5iHHGFWkhnTX9WsEAbunJCV2qcaXScKj4tTfvdDKfLiVuU2av6hbsMztirRze7LvYBkRHV3tGwyCptsrP\",\"client_ip\":\"%s\",\"client_port\":9708,\"node_ip\":\"%s\",\"node_port\":9707,\"services\":[\"VALIDATOR\"]},\"dest\":\"4PS3EDQ3dW1tci1Bp6543CfuuebjFrg36kLAUcskGfaA\"},\"metadata\":{\"from\":\"TWwCRQRZ2ZHMJFn9TzLp7W\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":4,\"txnId\":\"aa5e817d7cc626170eca175822029339a444eb0ee8f0bd20d3b0b76e566fb008\"},\"ver\":\"1\"}", testPoolIp, testPoolIp)
		};
//		String[] defaultTxns = new String[]{
//				"{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"OpsNode\",\"blskey\":\"4i39oJqm7fVX33gnYEbFdGurMtwYQJgDEYfXdYykpbJMWogByocaXxKbuXdrg3k9LP33Tamq64gUwnm4oA7FkxqJ5h4WfKH6qyVLvmBu5HgeV8Rm1GJ33mKX6LWPbm1XE9TfzpQXJegKyxHQN9ABquyBVAsfC6NSM4J5t1QGraJBfZi\",\"blskey_pop\":\"Qq3CzhSfugsCJotxSCRAnPjmNDJidDz7Ra8e4xvLTEzQ5w3ppGray9KynbGPH8T7XnUTU1ioZadTbjXaRY26xd4hQ3DxAyR4GqBymBn3UBomLRJHmj7ukcdJf9WE6tu1Fp1EhxmyaMqHv13KkDrDfCthgd2JjAWvSgMGWwAAzXEow5\",\"client_ip\":\"13.58.197.208\",\"client_port\":\"9702\",\"node_ip\":\"3.135.134.42\",\"node_port\":\"9701\",\"services\":[\"VALIDATOR\"]},\"dest\":\"EVwxHoKXUy2rnRzVdVKnJGWFviamxMwLvUso7KMjjQNH\"},\"metadata\":{\"from\":\"Pms5AZzgPWHSj6nNmJDfmo\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":1,\"txnId\":\"77ad6682f320be9969f70a37d712344afed8e3fba8d43fa5602c81b578d26088\"},\"ver\":\"1\"}",
//				"{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"cynjanode\",\"blskey\":\"32DLSweyJRxVMcVKGjUeNkVF1fwyFfRcFqGU9x7qL2ox2STpF6VxZkbxoLkGMPnt3gywRaY6jAjqgC8XMkf3webMJ4SEViPtBKZJjCCFTf4tGXfEsMwinummaPja85GgTALf7DddCNyCojmkXWHpgjrLx3626Z2MiNxVbaMapG2taFX\",\"blskey_pop\":\"RQRU8GVYSYZeu9dfH6myhzZ2qfxeVpCL3bTzgto1bRbx3QCt3mFFQQBVbgrqui2JpXhcWXxoDzp1WyYbSZwYqYQbRmvK7PPG82VAvVagv1n83Qa3cdyGwCevZdEzxuETiiXBRWSPfb4JibAXPKkLZHyQHWCEHcAEVeXtx7FRS1wjTd\",\"client_ip\":\"3.17.103.221\",\"client_port\":\"9702\",\"node_ip\":\"3.17.215.226\",\"node_port\":\"9701\",\"services\":[\"VALIDATOR\"]},\"dest\":\"iTq944JTtwHnst7rucfsRA4m26x9i6zCKKohETBCiWu\"},\"metadata\":{\"from\":\"QC174PGaL4zA9YHYqofPH2\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":2,\"txnId\":\"ce7361e44ec10a275899ece1574f6e38f2f3c7530c179fa07a2924e55775759b\"},\"ver\":\"1\"}",
//				"{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"GlobaliD\",\"blskey\":\"4Behdr1KJfLTAPNospghtL7iWdCHca6MZDxAtzYNXq35QCUr4aqpLu6p4Sgu9wNbTACB3DbwmVgE2L7hX6UsasuvZautqUpf4nC5viFpH7X6mHyqLreBJTBH52tSwifQhRjuFAySbbfyRK3wb6R2Emxun9GY7MFNuy792LXYg4C6sRJ\",\"blskey_pop\":\"RKYDRy8oTxKnyAV3HocapavH2jkw3PVe54JcEekxXz813DFbEy87N3i3BNqwHB7MH93qhtTRb7EZMaEiYhm92uaLKyubUMo5Rqjve2jbEdYEYVRmgNJWpxFKCmUBa5JwBWYuGunLMZZUTU3qjbdDXkJ9UNMQxDULCPU5gzLTy1B5kb\",\"client_ip\":\"13.56.175.126\",\"client_port\":\"9702\",\"node_ip\":\"50.18.84.131\",\"node_port\":\"9701\",\"services\":[\"VALIDATOR\"]},\"dest\":\"2ErWxamsNGBfhkFnwYgs4UW4aApct1kHUvu7jbkA1xX4\"},\"metadata\":{\"from\":\"4H8us7B1paLW9teANv8nam\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":3,\"txnId\":\"0c3b33b77e0419d6883be35d14b389c3936712c38a469ac5320a3cae68be1293\"},\"ver\":\"1\"}",
//				"{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"IdRamp\",\"blskey\":\"LoYzqUMPDZEfRshwGSzkgATxcM5FAS1LYx896zHnMfXP7duDsCQ6CBG2akBkZzgH3tBMvnjhs2z7PFc2gFeaKUF9fKDHhtbVqPofxH3ebcRfA959qU9mgvmkUwMUgwd21puRU6BebUwBiYxMxcE5ChReBnAkdAv19gVorm3prBMk94\",\"blskey_pop\":\"R1DjpsG7UxgwstuF7WDUL17a9Qq64vCozwJZ88bTrSDPwC1cdRn3WmhqJw5LpEhFQJosDSVVT6tS8dAZrrssRv2YsELbfGEJ7ZGjhNjZHwhqg4qeustZ7PZZE3Vr1ALSHY4Aa6KpNzGodxu1XymYZWXAFokPAs3Kho8mKcJwLCHn3h\",\"client_ip\":\"199.66.14.126\",\"client_port\":\"9702\",\"node_ip\":\"199.66.14.103\",\"node_port\":\"9701\",\"services\":[\"VALIDATOR\"]},\"dest\":\"5Zj5Aec6Kt9ki1runrXu87wZ522mnm3zwmaoHLUcHLx9\"},\"metadata\":{\"from\":\"AFLDFPoJuDQUHqnfmg8U7i\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":4,\"txnId\":\"c9df105558333ac8016610d9da5aad1e9a5dd50b9d9cc5684e94f439fa10f836\"},\"ver\":\"1\"}"
//		};

		FileWriter fw = new FileWriter(file);
		for (String defaultTxn : defaultTxns) {
			fw.write(defaultTxn);
			fw.write("\n");
		}

		fw.close();

	}

	public static String createPoolLedgerConfig() throws InterruptedException, ExecutionException, IndyException, IOException {
		createPoolLedgerConfig(DEFAULT_POOL_NAME);
		return DEFAULT_POOL_NAME;
	}

	private static void createPoolLedgerConfig(String poolName) throws IOException, InterruptedException, java.util.concurrent.ExecutionException, IndyException {
		File genesisTxnFile = createGenesisTxnFile("temp.txn");
		PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter
				= new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisTxnFile.getAbsolutePath());
		Pool.createPoolLedgerConfig(poolName, createPoolLedgerConfigJSONParameter.toJson()).get();
	}

	public static Pool createAndOpenPoolLedger() throws IndyException, InterruptedException, ExecutionException, IOException {
		String poolName = PoolUtils.createPoolLedgerConfig();

		PoolJSONParameters.OpenPoolLedgerJSONParameter config = new PoolJSONParameters.OpenPoolLedgerJSONParameter(null, null);
		return Pool.openPoolLedger(poolName, config.toJson()).get();
	}

	public static void deletePoolLedgerConfig() throws IndyException, InterruptedException, ExecutionException {
		Pool.deletePoolLedgerConfig(DEFAULT_POOL_NAME).get();
	}

	public interface PoolResponseChecker {
		boolean check(String response) throws JSONException;
	}

	public interface ActionChecker {
		String action() throws IndyException, ExecutionException, InterruptedException;
	}

	public static String ensurePreviousRequestApplied(Pool pool, String checkerRequest, PoolResponseChecker checker) throws IndyException, ExecutionException, InterruptedException {
		for (int i = 0; i < RESUBMIT_REQUEST_CNT; i++) {
			String response = Ledger.submitRequest(pool, checkerRequest).get();
			try {
				if (checker.check(response)) {
					return response;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				System.err.println(e.toString());
				System.err.println(response);
			}
			Thread.sleep(RESUBMIT_REQUEST_TIMEOUT);
		}
		throw new IllegalStateException();
	}

	public static boolean retryCheck(ActionChecker action, PoolResponseChecker checker) throws InterruptedException, ExecutionException, IndyException, JSONException {
		for (int i = 0; i < RESUBMIT_REQUEST_CNT; i++) {
			if (checker.check(action.action())) {
				return true;
			}
		}
		return false;
	}
}
