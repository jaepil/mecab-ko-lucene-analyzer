#!/bin/bash

version=$(grep -m 1 "<version>.*</version>" pom.xml | sed -n 's/.*>\(.*\)-SNAPSHOT.*/\1/p')
elasticsearch_plugin_version=$(grep -m 1 "<version>.*</version>" elasticsearch-analysis-mecab-ko/pom.xml | sed -n 's/.*>\(.*\)-SNAPSHOT.*/\1/p')
lucene_analyzer=mecab-ko-lucene-analyzer
mecab_loader=mecab-ko-mecab-loader
elasticsearch_analysis=elasticsearch-analysis-mecab-ko

# make Lucene/Solr package
dir=mecab-ko-lucene-analyzer-$version
mkdir $dir
cp lucene-analyzer/target/$lucene_analyzer-$version-SNAPSHOT.jar $dir/$lucene_analyzer-$version.jar
cp mecab-loader/target/$mecab_loader-$version-SNAPSHOT.jar $dir/$mecab_loader-$version.jar
tar czf $dir.tar.gz $dir
rm -rf $dir

# make ElasticSearch plugin
dir=$elasticsearch_analysis-$version
mkdir $dir
mkdir $dir/elasticsearch
cp lucene-analyzer/target/$lucene_analyzer-$version-SNAPSHOT.jar $dir/elasticsearch/$lucene_analyzer-$version.jar
cp mecab-loader/target/$mecab_loader-$version-SNAPSHOT.jar $dir/elasticsearch/$mecab_loader-$version.jar
cp elasticsearch-analysis-mecab-ko/target/$elasticsearch_analysis-$elasticsearch_plugin_version-SNAPSHOT.jar $dir/elasticsearch/$elasticsearch_analysis-$elasticsearch_plugin_version.jar
cp elasticsearch-analysis-mecab-ko/plugin-descriptor.properties $dir/elasticsearch/plugin-descriptor.properties
cp elasticsearch-analysis-mecab-ko/plugin-security.policy $dir/elasticsearch/plugin-security.policy
pushd $dir/elasticsearch
wget https://bitbucket.org/eunjeon/mecab-java/downloads/mecab-java-0.996.jar
popd
pushd $dir
zip -r $elasticsearch_analysis-$elasticsearch_plugin_version.zip elasticsearch
mv $elasticsearch_analysis-$elasticsearch_plugin_version.zip ../.
popd
rm -rf $dir
