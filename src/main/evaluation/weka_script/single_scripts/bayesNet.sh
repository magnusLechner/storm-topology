#BayesNet
#	Alpha 0.3 0.5 0.7 0.9
echo start BayesNet

java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.3 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row3_1.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row3_2.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.7 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row3_3.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.9 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row3_4.txt

java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.3 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row4_1.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row4_2.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.7 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row4_3.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.9 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row4_4.txt

java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.3 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row5_1.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row5_2.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.7 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row5_3.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.9 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row5_4.txt

java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.3 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row6_1.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row6_2.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.7 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row6_3.txt
java weka.classifiers.bayes.BayesNet -D -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.9 &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/BayesNet_row6_4.txt

echo stop BayesNet