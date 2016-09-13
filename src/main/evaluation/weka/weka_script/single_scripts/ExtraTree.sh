#ExtraTree
echo start ExtraTree

java -classpath ./weka.jar:$HOME/wekafiles/packages/extraTrees/extraTrees.jar weka.Run -no-load -no-scan weka.classifiers.trees.ExtraTree -K -1 -N -1 -S 1 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/ExtraTree_row3_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/extraTrees/extraTrees.jar weka.Run -no-load -no-scan weka.classifiers.trees.ExtraTree -K -1 -N -1 -S 1 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/ExtraTree_row4_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/extraTrees/extraTrees.jar weka.Run -no-load -no-scan weka.classifiers.trees.ExtraTree -K -1 -N -1 -S 1 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/ExtraTree_row5_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/extraTrees/extraTrees.jar weka.Run -no-load -no-scan weka.classifiers.trees.ExtraTree -K -1 -N -1 -S 1 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/ExtraTree_row6_1.txt

echo end ExtraTree